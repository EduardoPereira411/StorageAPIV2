package com.StorageAPI.Utils;

import com.StorageAPI.FileManagement.FileStorageService;
import com.StorageAPI.FileManagement.UploadFileResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class Utils {

    @Autowired
    private FileStorageService fileStorageService;

    public UploadFileResponse uploadFile(final String endpoint,final String endpointIdentification, final MultipartFile file) {

        final String fileName = fileStorageService.storeFile(String.valueOf(endpointIdentification), file);

        String fileDownloadUri = endpoint + endpointIdentification + "/" + fileName;

        return new UploadFileResponse(fileName, fileDownloadUri, file.getContentType(), file.getSize());
    }

    public void deleteFile(String fileUri) {
        fileStorageService.deleteFile(fileUri);
    }
}
