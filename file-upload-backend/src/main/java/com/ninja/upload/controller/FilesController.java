package com.ninja.upload.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import com.ninja.upload.message.ResponseMessage;
import com.ninja.upload.model.FileInfo;
import com.ninja.upload.service.FilesStorageService;

@Controller
public class FilesController {

	@Autowired
	private FilesStorageService storageService;
	
	@PostMapping("/upload")
	public ResponseEntity<ResponseMessage> uploadFile(@RequestParam("file") MultipartFile file) {
		String message = "";		
		try {
			storageService.save(file);
		
			message = "Uploaded the file successfully : " + file.getOriginalFilename();
			return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));			
		} catch (Exception e) {
			message = "Could not upload the file : " + file.getOriginalFilename() + ". Error : " + e.getMessage();
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
		}
	}
	
	@GetMapping("/files")
	public ResponseEntity<List<FileInfo>> getListFiles() {
		List<FileInfo> fileInfos = storageService.loadAll().map(path -> {
			String fileName = path.getFileName().toString();
			String url = MvcUriComponentsBuilder
					.fromMethodName(FilesController.class, "getFile", path.getFileName().toString()).build().toString();
			
			System.out.println("File Name : " + fileName + "  ======== " + " URL : " + url + " getListFiles");
			
			return new FileInfo(fileName, url);
		}).collect(Collectors.toList());
		
		return ResponseEntity.status(HttpStatus.OK).body(fileInfos);
	}
	
	/**
	 * 
	 * We have @Path expression to /files/{filename : .+}. 
	 * The .+ is a regular expression that will match any stream of characters after /files.
	 * So, the GET /files/work/citys.txt request would be routed to getFile().
	 */
	@GetMapping("/files/{filename:.+}")
	public ResponseEntity<Resource> getFile(@PathVariable String filename) {
		Resource file = storageService.load(filename);	
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment: filename=\"" + file.getFilename() + "\"").body(file);
	}
	
	@DeleteMapping("/files/{filename:.+}")
	public ResponseEntity<ResponseMessage> deleteFile(@PathVariable String filename) {
		String message = "";
		
		try {
			boolean ifFileDeleted = storageService.delete(filename);
			
			if (ifFileDeleted) {
				message = String.format("Delete the file successfully : %s", filename);
				return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
			}
			
			message = "The file does not exist!";
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage(message));
			
		} catch (Exception e) {
			message = String.format("Could not delete the file : %s. Error : %s", filename, e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage(message));
		}
	}
	
}
