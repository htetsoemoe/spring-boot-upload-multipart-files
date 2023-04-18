package com.ninja.upload;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FileUploadBackendApplication {

	public static void main(String[] args) {
		final Path path = Paths.get("uploads");
		System.out.println(path);
		
		SpringApplication.run(FileUploadBackendApplication.class, args);
		
	}

}
