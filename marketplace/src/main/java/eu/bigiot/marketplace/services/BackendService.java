package eu.bigiot.marketplace.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class BackendService {

	private static final Logger logger = LoggerFactory.getLogger(BackendService.class);

	public BackendService() {
		
	}
	
	public String getDesc() {
		return "This is a Marketplace for IoT offerings.";
	}

	
	public String getTitle() {
		return "IoT Marketplace";
	}

}