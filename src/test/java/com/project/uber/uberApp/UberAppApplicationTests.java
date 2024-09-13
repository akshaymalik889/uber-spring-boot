package com.project.uber.uberApp;

import com.project.uber.uberApp.services.EmailSenderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UberAppApplicationTests {

//	@Autowired
//	private EmailSenderService emailSenderService;

	@Test
	void contextLoads() {
		//emailSenderService.sendEmail("akshaymalik636@gmail.com", "testing email", "helllooo");
	}


//	@Test
//	void sendEmailMultiple() {
//
//		//creating array of emails
//		String emails[] = {
//				"akshaymalik636@gmail.com",
//				"202212008@gmail.com"
//		};
//
//		emailSenderService.sendEmail(emails, "testing email", "This is Testing Email");
//	}

}
