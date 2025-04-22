// package com.skillshare.skill_platform.service.Impl;



// import com.google.cloud.storage.Blob;
// import com.google.cloud.storage.BlobId;
// import com.google.cloud.storage.BlobInfo;
// import com.google.cloud.storage.Storage;
// import com.google.firebase.cloud.StorageClient;
// import com.skillshare.skill_platform.service.FileStorageService;

// import org.springframework.stereotype.Service;
// import org.springframework.web.multipart.MultipartFile;

// import java.io.IOException;
// import java.util.UUID;

// @Service
// public class FileStorageServiceImpl implements FileStorageService {

//     private final Storage storage;
//     private final String bucketName;

//     public FileStorageServiceImpl() {
//         this.storage = StorageClient.getInstance().bucket().getStorage();
//         this.bucketName = StorageClient.getInstance().bucket().getName();
//     }

//     @Override
//     public String uploadFile(MultipartFile file, String folder) throws IOException {
//         String filename = generateUniqueFilename(file.getOriginalFilename());
//         String path = folder + "/" + filename;
        
//         BlobId blobId = BlobId.of(bucketName, path);
//         BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
//                 .setContentType(file.getContentType())
//                 .build();
        
//         Blob blob = storage.create(blobInfo, file.getBytes());
        
//         return "https://storage.googleapis.com/" + bucketName + "/" + path;
//     }

//     @Override
//     public void deleteFile(String fileUrl) throws IOException {
//         if (fileUrl != null && fileUrl.contains(bucketName)) {
//             String path = fileUrl.substring(fileUrl.indexOf(bucketName) + bucketName.length() + 1);
//             BlobId blobId = BlobId.of(bucketName, path);
//             storage.delete(blobId);
//         }
//     }
    
//     private String generateUniqueFilename(String originalFilename) {
//         String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
//         return UUID.randomUUID().toString() + extension;
//     }
// }
