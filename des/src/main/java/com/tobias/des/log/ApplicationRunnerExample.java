package com.tobias.des.log;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class ApplicationRunnerExample implements ApplicationRunner {

	@Override
	public void run(ApplicationArguments args) throws Exception {
		try {
			// Başlatma sırasında çalıştırılacak kodlar
		} catch (Exception e) {
			// Hataları yakalayın ve loglayın
			System.err.println("Uygulama başlatılırken hata oluştu: " + e.getMessage());
		}
	}
}
