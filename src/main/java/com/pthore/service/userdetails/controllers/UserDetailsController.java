package com.pthore.service.userdetails.controllers;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.pthore.service.userdetails.dto.DomainDto;
import com.pthore.service.userdetails.dto.DomainUpdate;
import com.pthore.service.userdetails.dto.FollowersUpdate;
import com.pthore.service.userdetails.dto.GroupOperationRequest;
import com.pthore.service.userdetails.dto.GroupOperationResponse;
import com.pthore.service.userdetails.dto.PrivateUserProfileInfo;
import com.pthore.service.userdetails.dto.PublicUserProfileInfo;
import com.pthore.service.userdetails.dto.UserProfileUpdate;
import com.pthore.service.userdetails.entities.Domain;
import com.pthore.service.userdetails.services.UserProfileService;


@RestController
@RequestMapping(value = "/api/uds")
public class UserDetailsController {
	
	@Autowired
	private UserProfileService userProfileService;
	
	private Logger log = LoggerFactory.getLogger(UserDetailsController.class);

	/* An api to update the user profile.
	*/
	@PutMapping(value = "/update-profile") 
	public ResponseEntity<?> updateProfile (@RequestBody UserProfileUpdate userProfileUpdate) {
		// simply model map and update to the latest values. Note : DP change needs another API.
		userProfileService.updateUserProfile(userProfileUpdate);
		return ResponseEntity.ok().body("success");
	}
	
	
	/* An api to add or remove the followers of a user.
	*/
	@PostMapping(value = "/followers/{operation}") 
	public ResponseEntity<?> mangeFollowersRelatedOperations( 
			@PathVariable String operation, 
			@RequestBody FollowersUpdate followersUpdate) {
		
		// TODO: after the introduction of group feature this API needs to change.
		userProfileService.updateFollowers(followersUpdate);
		return ResponseEntity.ok().body("success");
	}
	
	
	
	/* An api to add or remove the Interested domains of a user.
	*/
	@PostMapping(value = "/domains/{operation}") 
	public ResponseEntity<?> manageUserRelatedDomainRelatedOperations( 
			@PathVariable String operation, 
			@RequestBody DomainUpdate domainUpdate) {
		
		userProfileService.updateUserInterestedDomain(domainUpdate);
		return ResponseEntity.ok().body("success");
	}
	
	/* An api to add new domain or remove or fetch the domains.
	*/
	@PostMapping(value = "/manage/domains/{operation}") 
	public ResponseEntity<?> manageGeneralDomainRelatedOperations( 
			@PathVariable String operation, 
			@RequestBody Domain domain) {
		
		List<DomainDto> domains = userProfileService.manageGeneralDomainRelatedOperations(operation, domain);
		return ResponseEntity.ok().body(domains);
	}
	
	/* An api to create groups, manage groups, add/remove users, 
	*/
	@PostMapping(value = "/groups/{operation}") 
	public ResponseEntity<?> mangeGroupRelatedOperations(@RequestBody GroupOperationRequest groupOperationRequest, 
			@PathVariable("operation") String operation,
			@RequestHeader Map<String, String> headers)  throws Exception {
		
		GroupOperationResponse response = this.userProfileService.manageGroupRelatedOperations(operation, groupOperationRequest);
		return ResponseEntity.ok(null);
	}
	

	/* An api to get basic userDetails (info, picture, followers)
	 *  This is needed to display author information in 
	*/
	@GetMapping(value= "/public-profile/{email}")
	public ResponseEntity<?> getPublicUserProfileInfo(@PathVariable String email) throws Exception {
		PublicUserProfileInfo publicUserProfile = userProfileService.getPublicUserProfileInfo(email);;
		return ResponseEntity.ok().body(publicUserProfile);
	}
	
	@PostMapping(value= "/update-learning-streak")
	public ResponseEntity<?> updateLearningStreak(@RequestBody String[] body) throws Exception {
		
		String email = body[0];
		Long learningStreak = Long.parseLong(body[1]);
		this.userProfileService.updateLearningStreak(email, learningStreak);
		return ResponseEntity.ok().body("success");
	}	
	
	/* An api to get Advanced userDetails (userActivity, followers, groups, interested domain, post recursion depth
	*/
	@GetMapping(value= "/all-user-details/{email}")
	public ResponseEntity<?> getAllUserProfileDetails(@PathVariable String email) throws Exception {
		
		PrivateUserProfileInfo privateUserProfileInfo = this.userProfileService.getUsersPrivateDetails(email);		
		return ResponseEntity.ok().body(privateUserProfileInfo);
	}
	

	/* An api to get global statistics for admin page.
	*/
	@GetMapping(value= "/user-stats/{email}")
	public ResponseEntity<?> getAllUserRelatedStats (@PathVariable String email) throws Exception {
		// TODO: NEED TO WRITE SIMPLE METHODS / ALGORITHMS TO AGGREGATE USEFUL INSIGHTS TO DISPLAY IN ADMIN PAGE.
		Object stats =  this.userProfileService.getGlobalStats();
		return ResponseEntity.ok().body(stats);
	}
	
	
	// API to create Thumbnail  and to resize the DP images and update the userProfile.
	@PostMapping(value = "/change-profile-picture") 
	public ResponseEntity<String> changeProfilePicture(@RequestParam("file")MultipartFile file) throws Exception {
		// update the profilePicture .
		userProfileService.updateProfilePicture(file);
		return ResponseEntity.ok().body("success");
	}
}
