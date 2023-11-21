package com.orsac.oiipcra.service;

import com.lowagie.text.DocumentException;
import com.orsac.oiipcra.bindings.OIIPCRAResponse;
import com.orsac.oiipcra.dto.AgencyDto;
import com.orsac.oiipcra.dto.BidderDetailsDto;
import com.orsac.oiipcra.dto.FormGDto;
import com.orsac.oiipcra.dto.TenderListDto;
import com.orsac.oiipcra.entities.TenderNoticePublishedEntity;
import com.orsac.oiipcra.entities.TenderPublished;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

@Service
public interface PdfService {
    //void draftTenderNotice(HttpServletResponse response, Integer tenderId, Integer issueNo) throws IOException, DocumentException;

    //void formG(HttpServletResponse exportResponse, Integer finYrId, String issueNo,Integer bidderId) throws IOException,DocumentException;

    File formG(HttpServletResponse exportResponse, Integer finYrId, String issueNo, Integer agencyId, Integer distId, Integer divisionId, Integer userId) throws IOException, Exception;

    void formG2(HttpServletResponse exportResponse, Integer finYrId, String issueNo, Integer bidderId) throws IOException, DocumentException;



    // void civilWorkTender(HttpServletResponse exportResponse, Integer finYrId, String issueNo,Integer bidderId) throws IOException;
    File technicalBidAbstract(HttpServletResponse exportResponse, Integer bidId, java.sql.Date bidOpeningDate, Integer bidStatus, Integer userId) throws IOException, Exception;
    File financialBidOpeningResult(HttpServletResponse exportResponse, Integer bidId, java.sql.Date finBidOpeningDate,Integer userId) throws IOException, Exception;

    //void TechnicalBidOpeningResult(HttpServletResponse exportResponse, Integer bidId, String bidOpeningDate) throws IOException, DocumentException, ParseException;

    File getFinancialBidOpeningAbstract(HttpServletResponse exportResponse, Integer bidId, java.sql.Date tenderOpeningDate,Integer userId) throws IOException, Exception;

    File getDraftLetterAward(HttpServletResponse exportResponse,Integer bidId,java.sql.Date technicalBidOpeningDate,java.sql.Date  financialBidOpeningDate,Integer DaysAllowedToSign,Integer NoticeIssueNo, Integer userId, Integer distId, Integer divisionId, java.sql.Date userDate) throws IOException, Exception;

    File abstractOfFinancialBidOpening(HttpServletResponse exportResponse, String bidId, Date bidOpeningDate) throws Exception;

    File draftTenderNotice(HttpServletResponse response, Integer bidId, String bidOpeningDate,Integer userId,Integer distId,Integer divisionId,String issueNo,String issueDate,List<Integer>distIds) throws Exception;

   // File getListOfBids(HttpServletResponse exportResponse, String bidId, String tenderOpeningDate) throws IOException, Exception;
    //File getListOfBids(HttpServletResponse exportResponse, String bidId, String tenderOpeningDate) throws IOException, Exception;

    File disbursementAndProjection(HttpServletResponse exportResponse) throws Exception;
    File getListOfBids(HttpServletResponse exportResponse, Integer bidId, java.sql.Date tenderOpeningDate, Integer userId) throws IOException, Exception;

    File statusOfMipsByMiDivision(HttpServletResponse exportResponse, Integer districtId, Integer divisionId) throws IOException,Exception;

    File expertPdfStatusOfMipsByDist(HttpServletResponse exportResponse, Integer districtId) throws Exception;

    File expertPdfExpenditureByDist(HttpServletResponse exportResponse, Integer districtId) throws Exception;

    File expertPdfExpenditureByDiv(HttpServletResponse exportResponse, Integer districtId, Integer divisionId) throws Exception;

    File expertPdfComponentEstdAndExp(HttpServletResponse exportResponse)throws Exception;
    OIIPCRAResponse getDraftTenderList(TenderListDto tenderListDto);

    File technicalBidOpeningResult(HttpServletResponse exportResponse, Integer bidId,  java.sql.Date bidOpeningDate,Integer distId, Integer divisionId, Integer userId) throws Exception;

    OIIPCRAResponse getFormGDetails(Integer finYrId, String issueNo, Integer agencyId,Integer userId);

    OIIPCRAResponse getAgencyDetailsByFinYr(Integer finYrId);

    File getBidderDataSheet(HttpServletResponse exportResponse, Integer bidId, java.sql.Date tenderOpeningDate, Integer agencyId,Integer userId) throws Exception;

    File getEngineerDatabase(HttpServletResponse exportResponse, /*Integer designationId,*/ Integer userId) throws IOException, Exception;

    List<BidderDetailsDto> getBidderByBidIdDD(Integer bidId);

    File getPackageWiseBidders(HttpServletResponse exportResponse, Integer bidId, java.sql.Date techBidDate, Integer packageId, Integer userId) throws IOException, Exception;

    File getTechnicalBids(HttpServletResponse exportResponse, Integer bidId, java.sql.Date techBidDate, Integer packageId, Integer userId) throws IOException, Exception ;

    File getTechnicalBidAttachments(HttpServletResponse exportResponse, Integer bidId, java.sql.Date techBidDate, Integer packageId, Integer userId) throws IOException, Exception;

    File getFinancialBid(HttpServletResponse exportResponse, Integer bidId, java.sql.Date techBidDate, Integer packageId, Integer userId) throws IOException, Exception;

    TenderNoticePublishedEntity saveDraftTenderNoticeLog(Integer bidId, String fileName,String bidOpeningDate);

    File preBidMeetingResult(HttpServletResponse exportResponse, Integer bidId, java.sql.Date bidOpeningDate, Integer userId) throws Exception;

    File getPackageWiseBidder(HttpServletResponse exportResponse, Integer bidId, java.sql.Date bidOpeningDate, Integer packageId, Integer userId) throws Exception;

    File getFinancialBidPdf(HttpServletResponse exportResponse, Integer bidId, java.sql.Date technicalBidDate, Integer packageId, Integer userId) throws Exception;
}