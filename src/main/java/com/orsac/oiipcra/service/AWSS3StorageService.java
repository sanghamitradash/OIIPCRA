package com.orsac.oiipcra.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface AWSS3StorageService {

    boolean uploadFileToS3(MultipartFile file, String assetId, String keyName);
    boolean uploadDocument(MultipartFile files, String contractId, String keyName);
    boolean uploadPublishedDocument(MultipartFile files, String contractId, String keyName);

    boolean uploadDraftTenderNoticeDocument(MultipartFile files, String contractId, String keyName);
    boolean uploadSurveyData(MultipartFile files, String contractId, String keyName);

    boolean uploadEstimateDocument(MultipartFile files, String contractId, String keyName);

    boolean uploadWorkProgressImage(MultipartFile files, String activityId, String keyName);
    boolean uploadAgencyImage(MultipartFile files, String agencyId, String keyName);

    boolean uploadSurveyorImage(MultipartFile files, String agencyId, String keyName);
    boolean uploadTenderDoc(MultipartFile files, String tenderId, String keyName);
    boolean uploadDocumentInvoice(MultipartFile files, String invoiceId, String keyName);

    boolean uploadIssueDocument(MultipartFile mult, String valueOf, String originalFilename);

    boolean uploadIssueTrackerImages(MultipartFile mult, String issueId, String originalFilename);

    boolean uploadDocument1(MultipartFile multi, String issueId,String originalFilename);

    boolean uploadGrievanceImage(MultipartFile files, String grievanceId, String originalFilename);

    boolean uploadGrievanceDocument(MultipartFile files, String grievanceId, String originalFilename);

    boolean uploadDraftTenderNoticeForPreviousTender(MultipartFile files, String id, String keyName);
    boolean uploadBidDocumentForPreviousTender(MultipartFile files, String id, String keyName);
    boolean uploadFeederImage(MultipartFile file, String feederId, String keyName);

    boolean uploadSurveyorImageDepth(MultipartFile file, String depthId, String keyName);

    boolean uploadCadImage(MultipartFile file, String cadId, String keyName);

    boolean uploadSurveyImage(MultipartFile file, String depthId, String keyName);

    boolean uploadFeederSurveyImages(MultipartFile mult, String feederId, String keyName);

    boolean uploadDepthSurveyImages(MultipartFile mult, String depthId, String keyName);

    boolean uploadCadSurveyImages(MultipartFile mult, String cadId, String keyName);
}
