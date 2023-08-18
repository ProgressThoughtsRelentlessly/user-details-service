package com.pthore.service.userdetails.services;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.pthore.service.userdetails.dao.IDomainRepository;
import com.pthore.service.userdetails.dao.IFollowersRepository;
import com.pthore.service.userdetails.dao.IPrivateGroupJoinRequestRepository;
import com.pthore.service.userdetails.dao.IPrivateGroupMemberRepository;
import com.pthore.service.userdetails.dao.IUserPostMetadataRepository;
import com.pthore.service.userdetails.dao.IUserProfileRepository;
import com.pthore.service.userdetails.dto.DomainDto;
import com.pthore.service.userdetails.dto.DomainUpdate;
import com.pthore.service.userdetails.dto.FollowersUpdate;
import com.pthore.service.userdetails.dto.GroupOperationRequest;
import com.pthore.service.userdetails.dto.GroupOperationResponse;
import com.pthore.service.userdetails.dto.PasswordChangeRequest;
import com.pthore.service.userdetails.dto.PrivateUserProfileInfo;
import com.pthore.service.userdetails.dto.PublicUserProfileInfo;
import com.pthore.service.userdetails.dto.UserProfileUpdate;
import com.pthore.service.userdetails.entities.Domain;
import com.pthore.service.userdetails.entities.Follower;
import com.pthore.service.userdetails.entities.Following;
import com.pthore.service.userdetails.entities.PrivateGroupJoinRequest;
import com.pthore.service.userdetails.entities.PrivateGroupMember;
import com.pthore.service.userdetails.entities.UserPostMetadata;
import com.pthore.service.userdetails.entities.UserProfile;
import com.pthore.service.userdetails.utils.AppConstant;
import com.pthore.service.userdetails.utils.ImageUtils;


@Service
@Transactional
public class UserProfileService {

	@Autowired
	private IUserProfileRepository userProfileRepository;
	
	@Autowired
	private IFollowersRepository followersRepository;
	
	@Autowired
	private IDomainRepository domainRepository;
	
	@Autowired
	private IUserPostMetadataRepository userPostMetadataRepository;
	
	@Autowired
	private IPrivateGroupMemberRepository privateGroupMemberRepository;
	
	@Autowired
	private IPrivateGroupJoinRequestRepository privateGroupJoinRequestRepository;
	
	@Autowired
	private RestTemplate restTemplate;
	
	private final Logger logger = LoggerFactory.getLogger(UserProfileService.class);

	
	public final void updateUserProfile(UserProfileUpdate userProfileUpdate) {
		
		String email = userProfileUpdate.getEmail();
		String firstname = userProfileUpdate.getFirstname();
		String lastname = userProfileUpdate.getLastname();
		String mobile = userProfileUpdate.getMobile();
		String description = userProfileUpdate.getDescription();
		String oldPassword = userProfileUpdate.getOldPassword();
		String newPassword = userProfileUpdate.getNewPassword();
		
		UserProfile userProfile = userProfileRepository.findByEmail(email);
		logger.info("fetched userProfile {}", userProfile.getEmail());
		if(userProfile != null) {
			// TODO: Add validation checks for each field.
			userProfile.setDescription(description);
			userProfile.setMobile(mobile);
			userProfile.setFirstname(firstname);
			userProfile.setLastname(lastname);
			
			if(oldPassword.length() > 0 && newPassword.length() > 0) {
				PasswordChangeRequest passwordChangeRequest = new PasswordChangeRequest(email, oldPassword, newPassword);
				boolean isPasswordUpdated = restTemplate.postForEntity(AppConstant.URI.AUTH_SERVICE_BASE_URL + "updatePassword", passwordChangeRequest, Boolean.class).getBody();
				logger.info(" Is Password updated for user = {} ? :  {}", email, isPasswordUpdated);
			}
			userProfileRepository.save(userProfile);
		
		}
	}
	
	public final void updateProfilePicture(MultipartFile file) throws Exception {
		try {
			InputStream imageInputStream = file.getInputStream();
			ImageUtils imageUtils = new ImageUtils();
			BufferedImage bufferedImageOutput = imageUtils.createThumbnail(ImageIO.read(imageInputStream), AppConstant.THUMBNAIL_IMAGE_DIMENSION);
			ByteArrayOutputStream imageOutputStream = new ByteArrayOutputStream();
			ImageIO.write(bufferedImageOutput, "png", imageOutputStream);
			byte[] resizedImage = imageOutputStream.toByteArray();
			String finalImageAsString = Base64.getEncoder().encodeToString(resizedImage);
			// TODO: save this image to UserAuth object.
			
		} catch (IOException e) {
			logger.info("error updating profile picture = {}", e.getMessage());
			throw new IllegalArgumentException();
		}
	}

	public final void updateFollowers(FollowersUpdate followersUpdate) {
		
		UserProfile myUserProfile = userProfileRepository.findByEmail(followersUpdate.getMyEmail());
		UserProfile targetUser = userProfileRepository.findByEmail(followersUpdate.getTargetEmail());
		
		if(followersUpdate.getOperation().toLowerCase().equals("add")) {
			
			Follower follower = new Follower();
			follower.setEmail(myUserProfile.getEmail());
			follower.setUser(targetUser);
			follower = followersRepository.save(follower);
		} else {
			
			followersRepository.deleteByEmail(myUserProfile.getEmail());
			// followersRepository.deleteByEmailAndUser(myUserProfile.getEmail(), targetUser);
		}
	}

	public final void updateUserInterestedDomain(DomainUpdate domainUpdate) {
		
		UserProfile user = userProfileRepository.findByEmail(domainUpdate.getEmail());
		Domain domain = domainRepository.findByDomainName(domainUpdate.getDomain());
		
		if(domainUpdate.getOperation().toLowerCase().equals("add")) {
			List<Domain> domainList = user.getDomains();
			if(Objects.isNull(domainList)) {
				domainList = new ArrayList<>();
				user.setDomains(domainList);
			}
			domainList.add(domain);
			userProfileRepository.save(user);
		} else {
			
			List<Domain> domainList = user.getDomains();
			if(Objects.isNull(domainList) || domainList.isEmpty()) 
				return;
			domainList.remove(domain); // Overridden equals in DOMAIN class
			userProfileRepository.save(user);
		}

	}

	public final PublicUserProfileInfo getPublicUserProfileInfo(String email) throws Exception {
	
		UserProfile user = userProfileRepository.findByEmail(email);
		List<UserPostMetadata> userPostMetadataList = userPostMetadataRepository.findByUserProfile(user);
		
		Map<String, Integer> monthlyPostCount = userPostMetadataList.stream()
				.filter(m ->  m.getPostCreationDate().getYear() == LocalDate.now().getYear())
				.collect(Collectors.toMap((m) -> 
					((UserPostMetadata) m).getPostCreationDate().getMonth().toString(), 
					(m) -> Integer.valueOf(1), 
					(oldVal, newVal) -> oldVal + newVal)
				);
		
		PublicUserProfileInfo userProfileInfo = new PublicUserProfileInfo();
		userProfileInfo.setEmail(email);
		userProfileInfo.setProfilePicture(user.getProfilePicture());
		userProfileInfo.setFollowersCount(Long.valueOf(user.getFollowers().size()));
		userProfileInfo.setMonthlyPostCount(monthlyPostCount);
		userProfileInfo.setSocialMediaLinks(user.getSocialMediaLinks());
		
		return userProfileInfo;
	}


	public final List<DomainDto> manageGeneralDomainRelatedOperations(String operation, Domain domain) {
		List<DomainDto> result = null;
		if(operation.equals("fetchAll") ) {
			
			result = this.domainRepository.findAll().stream().map(dom -> {
				DomainDto domainDto = new DomainDto();
				domainDto.setDomainDescription(dom.getDomainDescription());
				domainDto.setDomainName(dom.getDomainName());
				domainDto.setId(dom.getId());
				domainDto.setDomainLink(AppConstant.URI.POST_SERVICE_BASE_URL + "search/" + dom.getDomainName() + "/1");
				return domainDto;
			}).collect(Collectors.toList());
			
		} else if(operation.equals("addNew")) {
			// note: No validation check is done here. as this is a admin operation.
			this.domainRepository.save(domain);
		} else if(operation.equals("remove")) {
			// TODO This is a very unlikely operation. Which is done only by the admin. Hence will be implemented
			// in the future releases.
			// this.domainRepository.deleteDomainByName(domain.getDomainName());
		}
		return result;
	}
	// DONE:
	// 1. create a group: a button will trigger it. a only the property in the userProfile is going to change.
	//    	the above operation should be reversible, in which case all the private members and its related posts become 
	// 		public 
	// 2. add the user to the group. Only when group admin requests.
	// 3. queue a user request to join a private group.
	// 4. detach a user from the private group.
	// 5. fetch all pending private group join request. 
	// 6. Group owner should Accept or reject a user from joining the private group.
	// 7. Fetch all groups


	private final boolean isGroupOwnerRequest(GroupOperationRequest groupOperationRequest) {
		String jwt = groupOperationRequest.getJwt();
		String email = groupOperationRequest.getUserEmail();
		String[] body = new String[] {jwt, email};
		boolean isGroupOwner = restTemplate.postForEntity(
				AppConstant.URI.AUTH_SERVICE_BASE_URL + "verify-user", 
				body, 
				Boolean.class).getBody();
		return isGroupOwner;
	}
	
	private final List<PublicUserProfileInfo> getPublicUserProfileInfo(List<UserProfile> userProfiles) {
		
		return userProfiles.stream().map(userProfile -> {
			
			PublicUserProfileInfo userPublicProfileInfo = new PublicUserProfileInfo();
			userPublicProfileInfo.setEmail(userProfile.getEmail());
			userPublicProfileInfo.setProfilePicture(userProfile.getProfilePicture());
			userPublicProfileInfo.setFollowersCount(Long.valueOf(userProfile.getFollowers().size()));
			userPublicProfileInfo.setMonthlyPostCount(null);
			userPublicProfileInfo.setSocialMediaLinks(userProfile.getSocialMediaLinks());
			
			return userPublicProfileInfo;
		}).toList();
	}
	
	private final void createGroup(GroupOperationRequest groupOperationRequest) {
		
		UserProfile userProfile = this.userProfileRepository.findByEmail(groupOperationRequest.getUserEmail());
		boolean isGroupOwnerRequest = isGroupOwnerRequest(groupOperationRequest);
		userProfile.setGroup(true);
		this.userProfileRepository.save(userProfile);
	}
	
	private final void addUserToGroup(GroupOperationRequest groupOperationRequest) {
		if(isGroupOwnerRequest(groupOperationRequest)) {
			
			UserProfile toAddUserProfile = this.userProfileRepository.findByEmail(groupOperationRequest.getOtherUserEmail());
			
			PrivateGroupMember privateGroupMember = new PrivateGroupMember();
			privateGroupMember.setEmail(groupOperationRequest.getOtherUserEmail());
			privateGroupMember.setUser(toAddUserProfile);
			this.privateGroupMemberRepository.save(privateGroupMember);
		}
	}
	
	private final void joinPrivateGroup(GroupOperationRequest groupOperationRequest) {
		
		UserProfile requestingUser = this.userProfileRepository.findByEmail(groupOperationRequest.getUserEmail());
		UserProfile groupOwner = this.userProfileRepository.findByEmail(groupOperationRequest.getOtherUserEmail());
		PrivateGroupJoinRequest privateGroupJoinRequest = new PrivateGroupJoinRequest();
		privateGroupJoinRequest.setGroupOwner(groupOwner);
		privateGroupJoinRequest.setJoiner(requestingUser);
		privateGroupJoinRequest.setRequestStatus("requested");
		this.privateGroupJoinRequestRepository.save(privateGroupJoinRequest);
	}
	
	private final void removeUserFromPrivateGroup(GroupOperationRequest groupOperationRequest) {
		
		UserProfile requestingUser = this.userProfileRepository.findByEmail(groupOperationRequest.getUserEmail());
		UserProfile groupOwner = this.userProfileRepository.findByEmail(groupOperationRequest.getOtherUserEmail());
		groupOwner.getPrivateGroupMembers().removeIf(member -> member.getEmail().equals(requestingUser.getEmail()));
		this.userProfileRepository.save(groupOwner);
	}
	
	private final GroupOperationResponse fetchAllPendingJoinRequest(GroupOperationRequest groupOperationRequest) {
		
		GroupOperationResponse response = new GroupOperationResponse();
		List<UserProfile> pendingJoinRequestProfiles = null;
		
		if(isGroupOwnerRequest(groupOperationRequest)) {
			UserProfile groupOwner = this.userProfileRepository.findByEmail(groupOperationRequest.getUserEmail());
			List<PrivateGroupJoinRequest> pendingJoinRequest = groupOwner.getPrivateGroupJoinRequests();
			if(pendingJoinRequest != null) {
				pendingJoinRequestProfiles = pendingJoinRequest.stream().map(request -> request.getJoiner()).toList();
			}
		}
		response.setUsers(getPublicUserProfileInfo(pendingJoinRequestProfiles));
		return response;
	}
	
	private final void acceptOrRejectPrivateGroupJoinRequest(GroupOperationRequest groupOperationRequest) {
		
		if(isGroupOwnerRequest(groupOperationRequest)) {
			
			List<PublicUserProfileInfo> allUsers = groupOperationRequest.getUsers();
			List<String> statusList = groupOperationRequest.getGroupJoinStatuses();
			
			Set<String> acceptedToGroupUsersEmail = new HashSet<>();
			Set<String> rejectedUsersEmail = new HashSet<>();
			
			for(int idx = 0; idx < statusList.size(); idx++) {
				if(statusList.get(idx).equals("accepted")) {
					acceptedToGroupUsersEmail.add(allUsers.get(idx).getEmail());
				} else if(statusList.get(idx).equals("rejected")) {
					rejectedUsersEmail.add(allUsers.get(idx).getEmail());
				}
			}
			UserProfile groupOwner = this.userProfileRepository.findByEmail(groupOperationRequest.getUserEmail());
			List<PrivateGroupJoinRequest> allJoinRequests = groupOwner.getPrivateGroupJoinRequests();
			int idx = 0;
			while(allJoinRequests.size() > idx) {
				String email = allJoinRequests.get(idx).getJoinerEmail();
				if(acceptedToGroupUsersEmail.contains(email) || rejectedUsersEmail.contains(email)) {
					allJoinRequests.remove(idx);
					idx--;
				} 
				idx++;
			}
			List<UserProfile> acceptedUsers = this.userProfileRepository.findByEmailIn(acceptedToGroupUsersEmail);
			List<PrivateGroupMember> transformedAcceptedUsers = acceptedUsers.stream().map(user -> {
				PrivateGroupMember member = new PrivateGroupMember();
				member.setEmail(user.getEmail());
				member.setUser(user);
				return member;
			}).toList();
			this.privateGroupMemberRepository.saveAll(transformedAcceptedUsers);
		}
	}
	
	private final GroupOperationResponse fetchAllGroups() {
		
		GroupOperationResponse response = new GroupOperationResponse();
		List<UserProfile> allGroupAdmins = this.userProfileRepository.findByIsGroupEquals(true); // TODO: check if this is a feasible method.
		response.setUsers(getPublicUserProfileInfo(allGroupAdmins));
		return response;
	}
	
	
	public final GroupOperationResponse manageGroupRelatedOperations(String operation, GroupOperationRequest groupOperationRequest) {
		
		GroupOperationResponse response = new GroupOperationResponse();
		switch(operation) {
		
			case AppConstant.GROUP_OPERATIONS.CREATE_GROUP: 
				createGroup(groupOperationRequest);
				break;
				
			case AppConstant.GROUP_OPERATIONS.ADD_USER:
				addUserToGroup(groupOperationRequest);
				break;
				
			case AppConstant.GROUP_OPERATIONS.JOIN_PRIVATE_GROUP_REQUEST:
				joinPrivateGroup(groupOperationRequest);
				break;
				
			case AppConstant.GROUP_OPERATIONS.REMOVE_FROM_PRIVATE_GROUP:
				removeUserFromPrivateGroup(groupOperationRequest);
				break;
				
			case AppConstant.GROUP_OPERATIONS.FETCH_PENDING_JOIN_REQUEST:
				response = fetchAllPendingJoinRequest(groupOperationRequest);
				break;
				
			case AppConstant.GROUP_OPERATIONS.ACCEPT_REJECT_REQUEST:
				acceptOrRejectPrivateGroupJoinRequest(groupOperationRequest);
				break;
				
			case AppConstant.GROUP_OPERATIONS.FETCH_ALL_GROUPS:
				response = fetchAllGroups();
				break;
				
			default: 
				
		}
		
		return response;
	}

	public void updateLearningStreak(String email, Long learningStreak) {
		UserProfile userProfile = this.userProfileRepository.findByEmail(email);
		if(userProfile.getLearningStreak() < learningStreak) {
			userProfile.setLearningStreak(learningStreak);
			userProfileRepository.save(userProfile);
		}
	}
	
	public PrivateUserProfileInfo getUsersPrivateDetails(String email) {
		
		
		PrivateUserProfileInfo privateUserProfileInfo = new PrivateUserProfileInfo();
		UserProfile userProfile = this.userProfileRepository.findByEmail(email);
		
		List<UserPostMetadata> userPostMetadataList = userPostMetadataRepository.findByUserProfile(userProfile);
		
		Map<String, Integer> monthlyPostCount = userPostMetadataList.stream()
				.filter(m ->  m.getPostCreationDate().getYear() == LocalDate.now().getYear())
				.collect(Collectors.toMap((m) -> 
					((UserPostMetadata) m).getPostCreationDate().getMonth().toString(), 
					(m) -> Integer.valueOf(1), 
					(oldVal, newVal) -> oldVal + newVal)
				);
		
		Set<Following> allFollowings = userProfile.getFollowing();
		
		privateUserProfileInfo.setEmail(userProfile.getEmail());
		privateUserProfileInfo.setDescription(userProfile.getDescription());
		privateUserProfileInfo.setProfilePicture(userProfile.getProfilePicture());
		privateUserProfileInfo.setFollowersCount(Long.valueOf(allFollowings.size()));
		privateUserProfileInfo.setMonthlyPostCount(monthlyPostCount);
		privateUserProfileInfo.setSocialMediaLinks(userProfile.getSocialMediaLinks());
		
		Map<Boolean, List<Following>> partitionedUsers =  allFollowings.stream()
				.collect(Collectors.groupingBy(following -> following.getUser().isGroup()));
		List<Following> followingGroups = partitionedUsers.get(true);
		List<Following> followingUsers = partitionedUsers.get(false);
		List<Domain> followingDomains = userProfile.getDomains();
		Set<Follower> followers = userProfile.getFollowers();
		
		privateUserProfileInfo.setFollowers(followers);
		privateUserProfileInfo.setGroupsYouFollow(new HashSet<>(followingGroups));
		privateUserProfileInfo.setUsersYouFollow(new HashSet<>(followingUsers));
		privateUserProfileInfo.setDomainsYouFollow(new HashSet<>(followingDomains));
		privateUserProfileInfo.setLearningStreak(userProfile.getLearningStreak());
		
		
		return privateUserProfileInfo;
	}

	public Object getGlobalStats() {
		// TODO DETAILS YOU WANT TO DISPLAY IN THE DASHBOARD
		// get total users count
		// get total groups count
		// get total domains count
		// get total posts made so far, posts made in a day
		// total posts viewed in a day, week, month, year
		// total new posts creations in a day
		// popular author
		// user with highest learning streak
		// total visitors count in a day, week, month
		// if possible get the location of users and display the graph containing user vs location.
		
		
		
		return null;
	}

	

}
