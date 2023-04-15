package com.pthore.service.userdetails.utils;

import java.awt.Dimension;

import com.pthore.service.userdetails.entities.UserProfile;

public interface AppConstant {
	// configure it to get it from the property file or config server.
	public final String IMAGE_RESOURCE_BASE_URL = "http://localhost:8080/api/posts/download/";
	public final Dimension DESKTOP_IMAGE_DIMENSION = new Dimension(800, 600);
	public final Dimension MOBILE_IMAGE_DIMENSION = new Dimension(400, 600);
	public final Dimension THUMBNAIL_IMAGE_DIMENSION = new Dimension(200, 200);
	public final Long DEFAULT_PAGE_SIZE = 10L;
	
	public interface URI {
		public final String POST_SERVICE_BASE_URL = "http://localhost:8020/api/posts/";
		public final String AUTH_SERVICE_BASE_URL = "http://localhost:8000/api/";
	}
	public interface GROUP_OPERATIONS {
		
		String CREATE_GROUP = "create-group";
		String ADD_USER = "add-user";
		String JOIN_PRIVATE_GROUP_REQUEST = "request-to-join-private-group";
		String REMOVE_FROM_PRIVATE_GROUP = "remove-from-private-group";
		String FETCH_PENDING_JOIN_REQUEST = "fetch-all-pending-join-request";
		String ACCEPT_REJECT_REQUEST = "accept-reject-request";
		String FETCH_ALL_GROUPS = "fetch-all-groups";
		
	}
}
