package com.ninja.upload;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.ninja.upload.service.FilesStorageService;

import jakarta.annotation.Resource;

@SpringBootApplication
public class FileUploadBackendApplication implements CommandLineRunner{
	
	@Resource
	private FilesStorageService storageService;

	public static void main(String[] args) {		
		final Path path = Paths.get("uploads");
		System.out.println(path);
		
		SpringApplication.run(FileUploadBackendApplication.class, args);		
	}

	@Override
	public void run(String... args) throws Exception {
		storageService.init();
	}

}
