package com.orsac.oiipcra.serviceImpl;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.orsac.oiipcra.service.AWSS3StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
public class AWSS3StorageServiceImpl implements AWSS3StorageService {

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Autowired
    private AmazonS3 awsS3Client;

    @Override
    public boolean uploadFileToS3(MultipartFile file, String surveyId, String keyName) {
        boolean result = false;
        String bucketDestination = bucketName;
        if (!surveyId.equals("")) {
            bucketDestination = bucketName + "/" + surveyId;
        }
        String fileName = file.getOriginalFilename();
        if (!keyName.isEmpty() && !fileName.equals(keyName)) {
            fileName = keyName;
        }
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());
            PutObjectResult putObjectResult = awsS3Client.putObject(bucketDestination, fileName, file.getInputStream(), metadata);
            result = Boolean.parseBoolean(putObjectResult.getContentMd5());
            log.info("File upload is completed.");
            return result;
        } catch (AmazonServiceException | IOException ex) {
            log.info("File upload is failed.");
            log.error("Error= {} while uploading file.", ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    public boolean uploadDocument(MultipartFile files, String contractId, String keyName) {
        boolean result = false;
        String bucketDestination = bucketName;
        if (!contractId.equals("")) {
            bucketDestination = bucketName + "/" + "ContractDocument/" + contractId;
        }
        String fileName = files.getOriginalFilename();
        if (!keyName.isEmpty() && !fileName.equals(keyName)) {
            fileName = keyName;
        }
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(files.getSize());
            metadata.setContentType(files.getContentType());
            PutObjectResult putObjectResult = awsS3Client.putObject(bucketDestination, fileName, files.getInputStream(), metadata);
            result = Boolean.parseBoolean(putObjectResult.getContentMd5());
            log.info("Document upload is completed.");
            return result;
        } catch (AmazonServiceException | IOException ex) {
            log.info("Document upload is failed.");
            log.error("Error= {} while uploading Document.", ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    public boolean uploadPublishedDocument(MultipartFile files, String tenderId, String keyName) {
        boolean result = false;
        String bucketDestination = bucketName;
        if (!tenderId.equals("")) {
            bucketDestination = bucketName + "/" + "tenderPublished/" + tenderId;
        }
        String fileName = files.getOriginalFilename();
        if (!keyName.isEmpty() && !fileName.equals(keyName)) {
            fileName = keyName;
        }
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(files.getSize());
            metadata.setContentType(files.getContentType());
            PutObjectResult putObjectResult = awsS3Client.putObject(bucketDestination, fileName, files.getInputStream(), metadata);
            result = Boolean.parseBoolean(putObjectResult.getContentMd5());
            log.info("Document upload is completed.");
            return result;
        } catch (AmazonServiceException | IOException ex) {
            log.info("Document upload is failed.");
            log.error("Error= {} while uploading Document.", ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    public boolean uploadDraftTenderNoticeDocument(MultipartFile files, String tenderId, String keyName) {
        boolean result = false;
        String bucketDestination = bucketName;
        if (!tenderId.equals("")) {
            bucketDestination = bucketName + "/" + "DraftTenderNoticePrevious/" + tenderId;
        }
        String fileName = files.getOriginalFilename();
        if (!keyName.isEmpty() && !fileName.equals(keyName)) {
            fileName = keyName;
        }
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(files.getSize());
            metadata.setContentType(files.getContentType());
            PutObjectResult putObjectResult = awsS3Client.putObject(bucketDestination, fileName, files.getInputStream(), metadata);
            result = Boolean.parseBoolean(putObjectResult.getContentMd5());
            log.info("Document upload is completed.");
            return result;
        } catch (AmazonServiceException | IOException ex) {
            log.info("Document upload is failed.");
            log.error("Error= {} while uploading Document.", ex.getMessage());
            throw new RuntimeException(ex);
        }
    }
    public boolean uploadSurveyData(MultipartFile files, String tankId, String keyName) {
        boolean result = false;
        String bucketDestination = bucketName;
        if (!tankId.equals("")) {
            bucketDestination = bucketName + "/" + "SurveyJsonTextFile/" + tankId;
        }
        String fileName = files.getOriginalFilename();
        if (!keyName.isEmpty() && !fileName.equals(keyName)) {
            fileName = keyName;
        }
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(files.getSize());
            metadata.setContentType(files.getContentType());
            PutObjectResult putObjectResult = awsS3Client.putObject(bucketDestination, fileName, files.getInputStream(), metadata);
            result = Boolean.parseBoolean(putObjectResult.getContentMd5());
            log.info("SurveyData TextFile upload is completed.");
            return result;
        } catch (AmazonServiceException | IOException ex) {
            log.info("SurveyData TextFile upload is failed.");
            log.error("Error= {} while uploading Document.", ex.getMessage());
            throw new RuntimeException(ex);
        }
    }


    public boolean uploadGrievanceDocument(MultipartFile files, String grievanceId, String originalFilename) {
        boolean result = false;
        String bucketDestination = bucketName;
        if (!grievanceId.equals("")) {
            bucketDestination = bucketName + "/" + "Grievance/" + grievanceId;
        }
        String fileName = files.getOriginalFilename();
        if (!originalFilename.isEmpty() && !fileName.equals(originalFilename)) {
            fileName = originalFilename;
        }
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(files.getSize());
            metadata.setContentType(files.getContentType());
            PutObjectResult putObjectResult = awsS3Client.putObject(bucketDestination, fileName, files.getInputStream(), metadata);
            result = Boolean.parseBoolean(putObjectResult.getContentMd5());
            log.info("Document upload is completed.");
            return result;
        } catch (AmazonServiceException | IOException ex) {
            log.info("Document upload is failed.");
            log.error("Error= {} while uploading Document.", ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    public boolean uploadGrievanceImage(MultipartFile files, String grievanceId, String originalFilename) {
        boolean result = false;
        String bucketDestination = bucketName;
        if (!grievanceId.equals("")) {
            bucketDestination = bucketName + "/" + "Grievance/" + grievanceId;
        }
        String fileName = files.getOriginalFilename();
        if (!originalFilename.isEmpty() && !fileName.equals(originalFilename)) {
            fileName = originalFilename;
        }
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(files.getSize());
            metadata.setContentType(files.getContentType());
            PutObjectResult putObjectResult = awsS3Client.putObject(bucketDestination, fileName, files.getInputStream(), metadata);
            result = Boolean.parseBoolean(putObjectResult.getContentMd5());
            log.info("Document upload is completed.");
            return result;
        } catch (AmazonServiceException | IOException ex) {
            log.info("Document upload is failed.");
            log.error("Error= {} while uploading Document.", ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    public boolean uploadEstimateDocument(MultipartFile files, String estimateId, String keyName) {
        boolean result = false;
        String bucketDestination = bucketName;
        if (!estimateId.equals("")) {
            bucketDestination = bucketName + "/" + "EstimateDocument/" + estimateId;
        }
        String fileName = files.getOriginalFilename();
        if (!keyName.isEmpty() && !fileName.equals(keyName)) {
            fileName = keyName;
        }
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(files.getSize());
            metadata.setContentType(files.getContentType());
            PutObjectResult putObjectResult = awsS3Client.putObject(bucketDestination, fileName, files.getInputStream(), metadata);
            result = Boolean.parseBoolean(putObjectResult.getContentMd5());
            log.info("Estimate Document upload is completed.");
            return result;
        } catch (AmazonServiceException | IOException ex) {
            log.info("Document upload is failed.");
            log.error("Error= {} while uploading Document.", ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    public boolean uploadWorkProgressImage(MultipartFile files, String activityId, String keyName) {
        boolean result = false;
        String bucketDestination = bucketName;
        if (!activityId.equals("")) {
            bucketDestination = bucketName + "/" + "WorkProgress/" + activityId;
        }
        String fileName = files.getOriginalFilename();
        if (!keyName.isEmpty() && !fileName.equals(keyName)) {
            fileName = keyName;
        }
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(files.getSize());
            metadata.setContentType(files.getContentType());
            PutObjectResult putObjectResult = awsS3Client.putObject(bucketDestination, fileName, files.getInputStream(), metadata);
            result = Boolean.parseBoolean(putObjectResult.getContentMd5());
            log.info("Document upload is completed.");
            return result;
        } catch (AmazonServiceException | IOException ex) {
            log.info("Document upload is failed.");
            log.error("Error= {} while uploading Document.", ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    public boolean uploadAgencyImage(MultipartFile files, String agencyId, String keyName) {
        boolean result = false;
        String bucketDestination = bucketName;
        if (!agencyId.equals("")) {
            bucketDestination = bucketName + "/" + "AgencyImage/" + agencyId;
        }
        String fileName = files.getOriginalFilename();
        if (!keyName.isEmpty() && !fileName.equals(keyName)) {
            fileName = keyName;
        }
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(files.getSize());
            metadata.setContentType(files.getContentType());
            PutObjectResult putObjectResult = awsS3Client.putObject(bucketDestination, fileName, files.getInputStream(), metadata);
            result = Boolean.parseBoolean(putObjectResult.getContentMd5());
            log.info("Image upload is completed.");
            return result;
        } catch (AmazonServiceException | IOException ex) {
            log.info("Image upload is failed.");
            log.error("Error= {} while uploading Image.", ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    public boolean uploadSurveyorImage(MultipartFile files, String activityId, String keyName) {
        boolean result = false;
        String bucketDestination = bucketName;
        if (!activityId.equals("")) {
            bucketDestination = bucketName + "/" + "WorkProgress/" + activityId;
        }
        String fileName = files.getOriginalFilename();
        if (!keyName.isEmpty() && !fileName.equals(keyName)) {
            fileName = keyName;
        }
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(files.getSize());
            metadata.setContentType(files.getContentType());
            PutObjectResult putObjectResult = awsS3Client.putObject(bucketDestination, fileName, files.getInputStream(), metadata);
            result = Boolean.parseBoolean(putObjectResult.getContentMd5());
            log.info("SurveyorImage upload is completed.");
            return result;
        } catch (AmazonServiceException | IOException ex) {
            log.info("SurveyorImage upload is failed.");
            log.error("Error= {} while uploading Document.", ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    @Override
    public boolean uploadDocumentInvoice(MultipartFile files, String invoiceId, String keyName) {
        boolean result = false;
        String fileName = files.getOriginalFilename();
        String bucketDestination = bucketName;
        if (!invoiceId.equals("")) {
            bucketDestination = bucketName + "/" + "InvoiceDocument/" + invoiceId;
        }
        String fileName2 = fileName;
        if (!keyName.isEmpty() && !fileName2.equals(keyName)) {
            fileName2 = keyName;
        }
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(files.getSize());
            metadata.setContentType(files.getContentType());
            PutObjectResult putObjectResult = awsS3Client.putObject(bucketDestination, fileName2, files.getInputStream(), metadata);
            result = Boolean.parseBoolean(putObjectResult.getContentMd5());
            log.info("Document upload is completed.");
            return result;
        } catch (AmazonServiceException | IOException ex) {
            log.info("Document upload is failed.");
            log.error("Error= {} while uploading Document.", ex.getMessage());
            throw new RuntimeException(ex);
        }
    }


    /**
     * upload tender document
     */
    public boolean uploadTenderDoc(MultipartFile files, String tenderId, String keyName) {
        boolean result = false;
        String bucketDestination = bucketName;
        if (!tenderId.equals("")) {
            bucketDestination = bucketName + "/" + "TenderDocument/" + tenderId;
        }
        String fileName = files.getOriginalFilename();
        if (!keyName.isEmpty() && !fileName.equals(keyName)) {
            fileName = keyName;
        }
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(files.getSize());
            metadata.setContentType(files.getContentType());
            PutObjectResult putObjectResult = awsS3Client.putObject(bucketDestination, fileName, files.getInputStream(), metadata);
            result = Boolean.parseBoolean(putObjectResult.getContentMd5());
            log.info("Document upload is completed.");
            return result;
        } catch (AmazonServiceException | IOException ex) {
            log.info("Document upload is failed.");
            log.error("Error= {} while uploading Document.", ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    public boolean uploadIssueDocument(MultipartFile files, String issueId, String keyName) {
        boolean result = false;
        String bucketDestination = bucketName;
        if (!issueId.equals("")) {
            bucketDestination = bucketName + "/" + "IssueDocument/" + issueId;
        }
        String fileName = files.getOriginalFilename();
        if (!keyName.isEmpty() && !fileName.equals(keyName)) {
            fileName = keyName;
        }
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(files.getSize());
            metadata.setContentType(files.getContentType());
            PutObjectResult putObjectResult = awsS3Client.putObject(bucketDestination, fileName, files.getInputStream(), metadata);
            result = Boolean.parseBoolean(putObjectResult.getContentMd5());
            log.info("Document upload is completed.");
            return result;
        } catch (AmazonServiceException | IOException ex) {
            log.info("Document upload is failed.");
            log.error("Error= {} while uploading Document.", ex.getMessage());
            throw new RuntimeException(ex);
        }

    }

    public boolean uploadIssueTrackerImages(MultipartFile files, String issueId, String keyName) {
        boolean result = false;
        String bucketDestination = bucketName;
        if (!issueId.equals("")) {
            bucketDestination = bucketName + "/" + "IssueImage/" + issueId;
        }
        String fileName = files.getOriginalFilename();
        if (!keyName.isEmpty() && !fileName.equals(keyName)) {
            fileName = keyName;
        }
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(files.getSize());
            metadata.setContentType(files.getContentType());
            PutObjectResult putObjectResult = awsS3Client.putObject(bucketDestination, fileName, files.getInputStream(), metadata);
            result = Boolean.parseBoolean(putObjectResult.getContentMd5());
            log.info("Document upload is completed.");
            return result;
        } catch (AmazonServiceException | IOException ex) {
            log.info("Document upload is failed.");
            log.error("Error= {} while uploading Document.", ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    @Override
    public boolean uploadDocument1(MultipartFile multi, String issueId, String keyName) {
        boolean result = false;
        String bucketDestination = bucketName;
        if (!issueId.equals("")) {
            bucketDestination = bucketName + "/" + "IssueDocument/" + issueId;
        }
        String fileName = multi.getOriginalFilename();
        if (!keyName.isEmpty() && !fileName.equals(keyName)) {
            fileName = keyName;
        }
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(multi.getSize());
            metadata.setContentType(multi.getContentType());
            PutObjectResult putObjectResult = awsS3Client.putObject(bucketDestination, fileName, multi.getInputStream(), metadata);
            result = Boolean.parseBoolean(putObjectResult.getContentMd5());
            log.info("Document upload is completed.");
            return result;
        } catch (AmazonServiceException | IOException ex) {
            log.info("Document upload is failed.");
            log.error("Error= {} while uploading Document.", ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    public boolean uploadDraftTenderNoticeForPreviousTender(MultipartFile files, String id, String keyName) {
        boolean result = false;
        String bucketDestination = bucketName;
        if (!id.equals("")) {
            bucketDestination = bucketName + "/" + "DraftTenderNoticePrevious/" + id;
        }
        String fileName = files.getOriginalFilename();
        if (!keyName.isEmpty() && !fileName.equals(keyName)) {
            fileName = keyName;
        }
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(files.getSize());
            metadata.setContentType(files.getContentType());
            PutObjectResult putObjectResult = awsS3Client.putObject(bucketDestination, fileName, files.getInputStream(), metadata);
            result = Boolean.parseBoolean(putObjectResult.getContentMd5());
            log.info("DraftTender Document upload is completed.");
            return result;
        } catch (AmazonServiceException | IOException ex) {
            log.info("Document upload is failed.");
            log.error("Error= {} while uploading Document.", ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    @Override
    public boolean uploadBidDocumentForPreviousTender(MultipartFile files, String id, String keyName) {
        boolean result = false;
        String bucketDestination = bucketName;
        if (!id.equals("")) {
            bucketDestination = bucketName + "/" + "BidDocumentPrevious/" + id;
        }
        String fileName = files.getOriginalFilename();
        if (!keyName.isEmpty() && !fileName.equals(keyName)) {
            fileName = keyName;
        }
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(files.getSize());
            metadata.setContentType(files.getContentType());
            PutObjectResult putObjectResult = awsS3Client.putObject(bucketDestination, fileName, files.getInputStream(), metadata);
            result = Boolean.parseBoolean(putObjectResult.getContentMd5());
            log.info("Bid Document upload is completed.");
            return result;
        } catch (AmazonServiceException | IOException ex) {
            log.info("Document upload is failed.");
            log.error("Error= {} while uploading Document.", ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    @Override
    public boolean uploadFeederImage(MultipartFile file, String feederId, String keyName) {
        boolean result = false;
        String bucketDestination = bucketName;
        if (!feederId.equals("")) {
            bucketDestination = bucketName + "/" + "feederImage/" + feederId;
        }
        String fileName = file.getOriginalFilename();
        if (!keyName.isEmpty() && !fileName.equals(keyName)) {
            fileName = keyName;
        }
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());
            PutObjectResult putObjectResult = awsS3Client.putObject(bucketDestination, fileName, file.getInputStream(), metadata);
            result = Boolean.parseBoolean(putObjectResult.getContentMd5());
            log.info("feederImage upload is completed.");
            return result;
        } catch (AmazonServiceException | IOException ex) {
            log.info("feederImage upload is failed.");
            log.error("Error= {} while uploading Document.", ex.getMessage());
            throw new RuntimeException(ex);
        }
    }


    @Override
    public boolean uploadSurveyorImageDepth(MultipartFile file, String depthId, String keyName) {
        boolean result = false;
        String bucketDestination = bucketName;
        if (!depthId.equals("")) {
            bucketDestination = bucketName + "/" + "depthImage/" + depthId;
        }
        String fileName = file.getOriginalFilename();
        if (!keyName.isEmpty() && !fileName.equals(keyName)) {
            fileName = keyName;
        }
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());
            PutObjectResult putObjectResult = awsS3Client.putObject(bucketDestination, fileName, file.getInputStream(), metadata);
            result = Boolean.parseBoolean(putObjectResult.getContentMd5());
            log.info("SurveyorImage upload is completed.");
            return result;
        } catch (AmazonServiceException | IOException ex) {
            log.info("depthImage upload is failed.");
            log.error("Error= {} while uploading Document.", ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    @Override
    public boolean uploadCadImage(MultipartFile file, String cadId, String keyName) {
        boolean result = false;
        String bucketDestination = bucketName;
        if (!cadId.equals("")) {
            bucketDestination = bucketName + "/" + "cadImage/" + cadId;
        }
        String fileName = file.getOriginalFilename();
        if (!keyName.isEmpty() && !fileName.equals(keyName)) {
            fileName = keyName;
        }
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());
            PutObjectResult putObjectResult = awsS3Client.putObject(bucketDestination, fileName, file.getInputStream(), metadata);
            result = Boolean.parseBoolean(putObjectResult.getContentMd5());
            log.info("cadImage upload is completed.");
            return result;
        } catch (AmazonServiceException | IOException ex) {
            log.info("cadImage upload is failed.");
            log.error("Error= {} while uploading Document.", ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    @Override
    public boolean uploadSurveyImage(MultipartFile file, String depthId, String keyName) {
        boolean result = false;
        String bucketDestination = bucketName;
        if (!depthId.equals("")) {
            bucketDestination = bucketName + "/" + "depthImage/" + depthId;
        }
        String fileName = file.getOriginalFilename();
        if (!keyName.isEmpty() && !fileName.equals(keyName)) {
            fileName = keyName;
        }
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());
            PutObjectResult putObjectResult = awsS3Client.putObject(bucketDestination, fileName, file.getInputStream(), metadata);
            result = Boolean.parseBoolean(putObjectResult.getContentMd5());
            log.info("DepthImage upload is completed.");
            return result;
        } catch (AmazonServiceException | IOException ex) {
            log.info("DepthImage upload is failed.");
            log.error("Error= {} while uploading Document.", ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    @Override
    public boolean uploadFeederSurveyImages(MultipartFile mult, String feederId, String keyName) {
        boolean result = false;
        String bucketDestination = bucketName;
        if (!feederId.equals("")){
            bucketDestination = bucketName+ "/"+"feeder/" + feederId;
        }
        String fileName = mult.getOriginalFilename();
        if (!keyName.isEmpty() && !fileName.equals(keyName)){
            fileName = keyName;
        }
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(mult.getSize());
            metadata.setContentType(mult.getContentType());
            PutObjectResult putObjectResult = awsS3Client.putObject(bucketDestination, fileName, mult.getInputStream(),metadata);
            result= Boolean.parseBoolean(putObjectResult.getContentMd5());
            log.info("Issue Image upload is completed.");
            return result;
        } catch (AmazonServiceException | IOException ex) {
            log.info("Issue Image upload is failed.");
            log.error("Error= {} while uploading Document.", ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    @Override
    public boolean uploadDepthSurveyImages(MultipartFile mult, String depthId, String keyName) {
        boolean result = false;
        String bucketDestination = bucketName;
        if (!depthId.equals("")){
            bucketDestination = bucketName+ "/"+"depth/" + depthId;
        }
        String fileName = mult.getOriginalFilename();
        if (!keyName.isEmpty() && !fileName.equals(keyName)){
            fileName = keyName;
        }
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(mult.getSize());
            metadata.setContentType(mult.getContentType());
            PutObjectResult putObjectResult = awsS3Client.putObject(bucketDestination, fileName, mult.getInputStream(),metadata);
            result= Boolean.parseBoolean(putObjectResult.getContentMd5());
            log.info("Depth Survey Image upload is completed.");
            return result;
        } catch (AmazonServiceException | IOException ex) {
            log.info("Depth Image upload is failed.");
            log.error("Error= {} while uploading Document.", ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    @Override
    public boolean uploadCadSurveyImages(MultipartFile mult, String cadId, String keyName) {
        boolean result = false;
        String bucketDestination = bucketName;
        if (!cadId.equals("")){
            bucketDestination = bucketName+ "/"+"cad/" + cadId;
        }
        String fileName = mult.getOriginalFilename();
        if (!keyName.isEmpty() && !fileName.equals(keyName)){
            fileName = keyName;
        }
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(mult.getSize());
            metadata.setContentType(mult.getContentType());
            PutObjectResult putObjectResult = awsS3Client.putObject(bucketDestination, fileName, mult.getInputStream(),metadata);
            result= Boolean.parseBoolean(putObjectResult.getContentMd5());
            log.info("Cad Survey Image upload is completed.");
            return result;
        } catch (AmazonServiceException | IOException ex) {
            log.info("Cad Image upload is failed.");
            log.error("Error= {} while uploading Document.", ex.getMessage());
            throw new RuntimeException(ex);
        }
    }
}





