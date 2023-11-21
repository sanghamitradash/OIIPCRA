package com.orsac.oiipcra.controller;

import com.amazonaws.util.IOUtils;
import com.orsac.oiipcra.bindings.OIIPCRAResponse;
import com.orsac.oiipcra.dto.*;
import com.orsac.oiipcra.entities.TenderNoticePublishedEntity;
import com.orsac.oiipcra.entities.TenderPublished;
import com.orsac.oiipcra.service.AWSS3StorageService;
import com.orsac.oiipcra.service.PdfService;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
//import sun.swing.BakedArrayList;

import javax.persistence.criteria.CriteriaBuilder;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/pdf")
@Slf4j
public class PdfController {
    @Autowired
    private PdfService pdfService;
    @Autowired
    private AWSS3StorageService awss3StorageService;

//    @PostMapping("/draftTenderNotice")
//    public void draftTenderNotice(HttpServletResponse exportResponse,@RequestParam Integer tenderId,@RequestParam Integer issueNo ,
//                                  @RequestParam String issueDate) throws IOException {
//        exportResponse.setContentType("application/pdf");
//        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
//        String currentDateTime = dateFormatter.format(new Date());
//
//        String headerKey = "Content-Disposition";
//        String headerValue = "attachment; filename=tank_" + currentDateTime + ".pdf";
//        exportResponse.setHeader(headerKey, headerValue);
//
//        try {
//            pdfService.draftTenderNotice(exportResponse,tenderId,issueNo);
//        } catch (Exception e) {
//            log.info("Error in exportpdf"+e.getMessage());
//        }
//    }


    @PostMapping("/formG2")
    public void formG2(HttpServletResponse exportResponse,@RequestParam(value = "finYrId", required = false) Integer finYrId,
                      @RequestParam(value = "issueNo", required = false) String issueNo,
                      @RequestParam(value = "bidderId", required = false) Integer bidderId ) throws IOException
    {
        exportResponse.setContentType("application/pdf");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=tank_" + currentDateTime + ".pdf";
        exportResponse.setHeader(headerKey, headerValue);

        try {
            pdfService.formG2(exportResponse,finYrId,issueNo,bidderId);
        } catch (Exception e) {
            log.info("Error in exportpdf"+e.getMessage());
        }
    }

    @PostMapping("/formG")
    public void formG(HttpServletResponse exportResponse,@RequestParam(value = "finYrId", required = false) Integer finYrId,
                      @RequestParam(value = "issueNo", required = false) String issueNo,
                      @RequestParam(value = "agencyId", required = false) Integer agencyId,
                      @RequestParam(value = "distId",required = false)Integer distId,
                      @RequestParam(value=  "divisionId",required = false)Integer divisionId,
                      @RequestParam(value=  "userId",required = false)Integer userId)throws IOException
    {
        try {
            Path file = Paths.get(pdfService.formG(exportResponse,finYrId,issueNo,agencyId,distId,divisionId,userId).getAbsolutePath());
            if(Files.exists(file)){
                exportResponse.setContentType("application/pdf");
                exportResponse.addHeader("Content-Disposition", "attachment; filename"+ file.getFileName());
                Files.copy(file, exportResponse.getOutputStream());
                exportResponse.getOutputStream().flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @PostMapping("/exportPdfPackageWiseBidders")
    public void getPackageWiseBidders(HttpServletResponse exportResponse,@RequestParam(value = "bidId", required = false) Integer bidId,
                                      @RequestParam(value=  "techBidDate",required = false)java.sql.Date techBidDate,
                                      @RequestParam(value=  "agencyId",required = false)Integer agencyId,
                                      @RequestParam(value=  "userId",required = false)Integer userId)throws IOException
    {
        try {
            Path file = Paths.get(pdfService.getPackageWiseBidders(exportResponse, bidId, techBidDate, agencyId, userId).getAbsolutePath());
            if(Files.exists(file)){
                exportResponse.setContentType("application/pdf");
                exportResponse.addHeader("Content-Disposition", "attachment; filename"+ file.getFileName());
                Files.copy(file, exportResponse.getOutputStream());
                exportResponse.getOutputStream().flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/exportPdfTechnicalBid")
    public void getTechnicalBid(HttpServletResponse exportResponse,@RequestParam(value = "bidId", required = false) Integer bidId,
                                      @RequestParam(value = "techBidDate", required = false) java.sql.Date techBidDate,
                                      @RequestParam(value = "packageId", required = false) Integer packageId,
                                      @RequestParam(value=  "userId",required = false)Integer userId)throws IOException
    {
        try {
            Path file = Paths.get(pdfService.getTechnicalBids(exportResponse, bidId, techBidDate, packageId, userId).getAbsolutePath());
            if(Files.exists(file)){
                exportResponse.setContentType("application/pdf");
                exportResponse.addHeader("Content-Disposition", "attachment; filename"+ file.getFileName());
                Files.copy(file, exportResponse.getOutputStream());
                exportResponse.getOutputStream().flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/exportPdfTechnicalBidAttachments")
    public void getTechnicalBidAttachments(HttpServletResponse exportResponse,@RequestParam(value = "bidId", required = false) Integer bidId,
                                @RequestParam(value = "techBidDate", required = false) java.sql.Date techBidDate,
                                @RequestParam(value = "packageId", required = false) Integer packageId,
                                @RequestParam(value=  "userId",required = false)Integer userId)throws IOException
    {
        try {
            Path file = Paths.get(pdfService.getTechnicalBidAttachments(exportResponse, bidId, techBidDate, packageId, userId).getAbsolutePath());
            if(Files.exists(file)){
                exportResponse.setContentType("application/pdf");
                exportResponse.addHeader("Content-Disposition", "attachment; filename"+ file.getFileName());
                Files.copy(file, exportResponse.getOutputStream());
                exportResponse.getOutputStream().flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/exportPdfFinancialBid")
    public void getFinancialBid(HttpServletResponse exportResponse,@RequestParam(value = "bidId", required = false) Integer bidId,
                                      @RequestParam(value = "techBidDate", required = false) java.sql.Date techBidDate,
                                      @RequestParam(value = "packageId", required = false) Integer packageId,
                                      @RequestParam(value=  "userId",required = false)Integer userId)throws IOException
    {
        try {
            Path file = Paths.get(pdfService.getFinancialBid(exportResponse, bidId, techBidDate, packageId, userId).getAbsolutePath());
            if(Files.exists(file)){
                exportResponse.setContentType("application/pdf");
                exportResponse.addHeader("Content-Disposition", "attachment; filename"+ file.getFileName());
                Files.copy(file, exportResponse.getOutputStream());
                exportResponse.getOutputStream().flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/technicalBidOpeningResult")
    public void technicalBidOpeningResult(HttpServletResponse exportResponse,@RequestParam(value = "bidId", required = false) Integer bidId,
                                          @RequestParam(value = "bidOpeningDate", required = false) java.sql.Date bidOpeningDate,
                                          @RequestParam(value = "distId",required = false)Integer distId,
                                          @RequestParam(value=  "divisionId",required = false)Integer divisionId,
                                          @RequestParam(value="userId")Integer userId)throws IOException
    {
        try {
            Path file = Paths.get(pdfService.technicalBidOpeningResult(exportResponse,bidId,bidOpeningDate,distId,divisionId,userId).getAbsolutePath());
            if(Files.exists(file)){
                exportResponse.setContentType("application/pdf");
                exportResponse.addHeader("Content-Disposition", "attachment; filename"+ file.getFileName());
                Files.copy(file, exportResponse.getOutputStream());
                exportResponse.getOutputStream().flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/technicalBidAbstract")
    public void technicalBidAbstract(HttpServletResponse exportResponse,@RequestParam(value = "bidId", required = false) Integer bidId,
                                 @RequestParam(value = "bidOpeningDate", required = false)   java.sql.Date bidOpeningDate,
                                 @RequestParam(value = "bidStatus", required = false) Integer bidStatus,
                                @RequestParam(value = "userId", required = false) Integer userId) throws IOException
    {
        try {
            Path file = Paths.get(pdfService.technicalBidAbstract(exportResponse, bidId, bidOpeningDate,bidStatus, userId).getAbsolutePath());
            if(Files.exists(file)){
                exportResponse.setContentType("application/pdf");
                exportResponse.addHeader("Content-Disposition", "attachment; filename"+ file.getFileName());
                Files.copy(file, exportResponse.getOutputStream());
                exportResponse.getOutputStream().flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


//    @PostMapping("/technicalBidOpeningResult")
//    public void TechnicalBidOpeningResult(HttpServletResponse exportResponse,@RequestParam(value = "bidId", required = false) Integer bidId,
//                      @RequestParam(value = "bidOpeningDate", required = false)  String bidOpeningDate) throws IOException
//    {
//        exportResponse.setContentType("application/pdf");
//        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
//        String currentDateTime = dateFormatter.format(new Date());
//
//        String headerKey = "Content-Disposition";
//        String headerValue = "attachment; filename=tank_" + currentDateTime + ".pdf";
//        exportResponse.setHeader(headerKey, headerValue);
//
//        try {
//            pdfService.TechnicalBidOpeningResult(exportResponse,bidId,bidOpeningDate);
//        } catch (Exception e) {
//            log.info("Error in exportpdf"+e.getMessage());
//        }
//    }




    @PostMapping("/financialBidOpeningResult")
    public void financialBidOpeningResult(HttpServletResponse exportResponse,@RequestParam(value = "bidId") Integer bidId,
                                          @RequestParam(value = "finBidOpeningDate")  java.sql.Date finBidOpeningDate,
                                          @RequestParam(value = "userId")  Integer userId) throws IOException
    {
        try {
            Path file = Paths.get(pdfService.financialBidOpeningResult(exportResponse,bidId,finBidOpeningDate, userId).getAbsolutePath());
            if(Files.exists(file)){
                exportResponse.setContentType("application/pdf");
                exportResponse.addHeader("Content-Disposition", "attachment; filename"+ file.getFileName());
                Files.copy(file, exportResponse.getOutputStream());
                exportResponse.getOutputStream().flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/getFinancialBidOpeningAbstract")
    public void getFinancialBidOpeningAbstract(HttpServletResponse exportResponse,@RequestParam(value = "bidId", required = false) Integer bidId,
                                               @RequestParam(value = "tenderOpeningDate", required = false)   java.sql.Date tenderOpeningDate,
                                               @RequestParam(value = "userId", required = false) Integer userId) throws IOException {
        try {
            Path file = Paths.get(pdfService.getFinancialBidOpeningAbstract(exportResponse, bidId, tenderOpeningDate, userId).getAbsolutePath());
            if (Files.exists(file)) {
                exportResponse.setContentType("application/pdf");
                exportResponse.addHeader("Content-Disposition", "attachment; filename" + file.getFileName());
                Files.copy(file, exportResponse.getOutputStream());
                exportResponse.getOutputStream().flush();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @PostMapping("/getDraftLetterAward")
    public void getDraftLetterAward(HttpServletResponse exportResponse, @RequestParam(value = "bidId", required = false) Integer bidId,
                                    @RequestParam(value = "technicalBidOpeningDate", required = false) java.sql.Date technicalBidOpeningDate,
                                    @RequestParam(value = "financialBidOpeningDate", required = false) java.sql.Date financialBidOpeningDate,
                                    @RequestParam(value = "DaysAllowedToSign", required = false) Integer DaysAllowedToSign,
                                    @RequestParam(value = "NoticeIssueNo", required = false) Integer NoticeIssueNo,
                                    @RequestParam(value=  "userId",required = false)Integer userId,
                                    @RequestParam(value = "distId",required = false)Integer distId,
                                    @RequestParam(value=  "divisionId",required = false)Integer divisionId,
                                    @RequestParam(value = "userDate", required = false) java.sql.Date userDate) throws IOException {
        try{
            Path file = Paths.get(pdfService.getDraftLetterAward(exportResponse, bidId, technicalBidOpeningDate, financialBidOpeningDate,DaysAllowedToSign,NoticeIssueNo,userId,distId,divisionId,userDate).getAbsolutePath());
            if (Files.exists(file)) {
                exportResponse.setContentType("application/pdf");
                exportResponse.addHeader("Content-Disposition", "attachment; filename" + file.getFileName());
                Files.copy(file, exportResponse.getOutputStream());
                exportResponse.getOutputStream().flush();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }


    @PostMapping("/getBidderDataSheet")
    public void getBidderDataSheet(HttpServletResponse exportResponse, @RequestParam(value = "bidId", required = false) Integer bidId,
                                    @RequestParam(value = "tenderOpeningDate", required = false) java.sql.Date tenderOpeningDate,
                                    @RequestParam(value = "agencyId", required = false) Integer agencyId,
                                   @RequestParam(value = "userId", required = false) Integer userId) throws IOException {
        try{
            Path file = Paths.get(pdfService.getBidderDataSheet(exportResponse, bidId, tenderOpeningDate, agencyId,userId).getAbsolutePath());
            if (Files.exists(file)) {
                exportResponse.setContentType("application/pdf");
                exportResponse.addHeader("Content-Disposition", "attachment; filename" + file.getFileName());
                Files.copy(file, exportResponse.getOutputStream());
                exportResponse.getOutputStream().flush();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }

    //Abstract of Financial BID Opening
    @PostMapping("/exportPdfAbstractOfFinancialBidOpening")
    public void abstractOfFinancialBidOpening(HttpServletResponse exportResponse,@RequestParam(value = "bidId", required = false) String bidId,
                                              @RequestParam(value = "bidOpeningDate", required = false) Date bidOpeningDate){
        try{
            Path file = Paths.get(pdfService.abstractOfFinancialBidOpening(exportResponse,bidId,bidOpeningDate).getAbsolutePath());
            if(Files.exists(file)) {
                exportResponse.setContentType("application/pdf");
                exportResponse.addHeader("Content-Disposition", "attachment; filename" + file.getFileName());
                Files.copy(file, exportResponse.getOutputStream());
                exportResponse.getOutputStream().flush();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Draft tender notice
    @PostMapping("/exportPdfDraftTenderNotice")
    public void draftTenderNotice(HttpServletResponse exportResponse, @RequestParam(value = "bidId", required = false) Integer bidId,
                                  @RequestParam(value = "bidOpeningDate", required = false) String bidOpeningDate,
                                  @RequestParam(value = "issueDate",required = false)String issueDate,
                                  @RequestParam(value = "userId",required = false)Integer userId,
                                  @RequestParam(value = "distId",required = false)Integer distId,
                                  @RequestParam(value = "distIds",required = false) List<Integer>distIds,
                                  @RequestParam(value = "divisionId",required = false)Integer divisionId,
                                  @RequestParam(value = "issueNo",required = false)String issueNo) {
        try {
            System.out.println(bidId);
            Path file = Paths.get(pdfService.draftTenderNotice(exportResponse, bidId, bidOpeningDate, distId, divisionId, userId, issueNo, issueDate,distIds).getAbsolutePath());
            File draftFile=pdfService.draftTenderNotice(exportResponse, bidId, bidOpeningDate,distId, divisionId, userId, issueNo, issueDate,distIds);
            String fileName= String.valueOf(file.getFileName());


            FileInputStream input = new FileInputStream(draftFile);
            MultipartFile multipartFile = new MockMultipartFile("file",
                    draftFile.getName(), "application/pdf", IOUtils.toByteArray(input));
          /*  MultipartFile multipartFile = new MockMultipartFile("file",
                    draftFile.getName(), "text/plain", IOUtils.toString(input).getBytes());*/
            TenderNoticePublishedEntity tenderPublished=pdfService.saveDraftTenderNoticeLog(bidId,fileName,bidOpeningDate);
            boolean saveDocument = awss3StorageService.uploadDraftTenderNoticeDocument(multipartFile, String.valueOf(tenderPublished.getId()),fileName);

            if (Files.exists(file)) {
                exportResponse.setContentType("application/pdf");
                exportResponse.addHeader("Content-Disposition", "attachment; filename" + file.getFileName());
                Files.copy(file, exportResponse.getOutputStream());
                exportResponse.getOutputStream().flush();
                // return "here";
            }
            // return "file nf";
        } catch (Exception e) {
            e.printStackTrace();
            // return e.getMessage();
//            return "exception";
        }
    }


    @PostMapping("/exportPdfListOfBids")
    public void getListOfBids(HttpServletResponse exportResponse, @RequestParam(value = "bidId", required = false) Integer bidId,
                              @RequestParam(value = "tenderOpeningDate", required = false) java.sql.Date tenderOpeningDate,
                              @RequestParam(value = "userId", required = false) Integer userId) throws IOException {
        try {
            Path file = Paths.get(pdfService.getListOfBids(exportResponse, bidId, tenderOpeningDate, userId).getAbsolutePath());
            if (Files.exists(file)) {
                exportResponse.setContentType("application/pdf");
                exportResponse.addHeader("Content-Disposition", "attachment; filename" + file.getFileName());
                Files.copy(file, exportResponse.getOutputStream());
                exportResponse.getOutputStream().flush();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @PostMapping("/exportPdfEngineerDatabase")
    public void getListOfBids(HttpServletResponse exportResponse, /*@RequestParam(value = "designationId", required = false) Integer designationId,*/
                              @RequestParam(value = "userId", required = false) Integer userId) throws IOException, Exception {
        try {
            Path file = Paths.get(pdfService.getEngineerDatabase(exportResponse, /*designationId,*/ userId).getAbsolutePath());
            if (Files.exists(file)) {
                exportResponse.setContentType("application/pdf");
                exportResponse.addHeader("Content-Disposition", "attachment; filename" + file.getFileName());
                Files.copy(file, exportResponse.getOutputStream());
                exportResponse.getOutputStream().flush();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @PostMapping("/exportPdfDisbursementAndProjection")
    public void exportPdfDisbursementAndProjection(HttpServletResponse exportResponse){
        try{
            Path file = Paths.get(pdfService.disbursementAndProjection(exportResponse).getAbsolutePath());
            if(Files.exists(file)) {
                exportResponse.setContentType("application/pdf");
                exportResponse.addHeader("Content-Disposition", "attachment; filename" + file.getFileName());
                Files.copy(file, exportResponse.getOutputStream());
                exportResponse.getOutputStream().flush();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/expertPdfStatusOfMipsByMiDivision")
    public void expertPdfStatusOfMipsByMiDivision(HttpServletResponse exportResponse,@RequestParam(required = false,defaultValue = "0") Integer districtId,
                                                  @RequestParam(required = false,defaultValue = "0")Integer divisionId){
        try{
            Path file = Paths.get(pdfService.statusOfMipsByMiDivision(exportResponse,districtId,divisionId).getAbsolutePath());
            if(Files.exists(file)) {
                exportResponse.setContentType("application/pdf");
                exportResponse.addHeader("Content-Disposition", "attachment; filename" + file.getFileName());
                Files.copy(file, exportResponse.getOutputStream());
                exportResponse.getOutputStream().flush();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    @PostMapping("/expertPdfStatusOfMipsByDist")
    public void expertPdfStatusOfMipsByDist(HttpServletResponse exportResponse,@RequestParam(required = false,defaultValue = "0") Integer districtId)
    {
        try{
            Path file = Paths.get(pdfService.expertPdfStatusOfMipsByDist(exportResponse,districtId).getAbsolutePath());
            if(Files.exists(file)) {
                exportResponse.setContentType("application/pdf");
                exportResponse.addHeader("Content-Disposition", "attachment; filename" + file.getFileName());
                Files.copy(file, exportResponse.getOutputStream());
                exportResponse.getOutputStream().flush();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/expertPdfExpenditureByDist")
    public void expertPdfExpenditureByDist(HttpServletResponse exportResponse,@RequestParam(required = false,defaultValue = "0") Integer districtId)
    {
        try{
            Path file = Paths.get(pdfService.expertPdfExpenditureByDist(exportResponse,districtId).getAbsolutePath());
            if(Files.exists(file)) {
                exportResponse.setContentType("application/pdf");
                exportResponse.addHeader("Content-Disposition", "attachment; filename" + file.getFileName());
                Files.copy(file, exportResponse.getOutputStream());
                exportResponse.getOutputStream().flush();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    @PostMapping("/getDraftTenderList")
    public OIIPCRAResponse getDraftTenderList(@RequestBody TenderListDto tenderListDto) {
        return pdfService.getDraftTenderList(tenderListDto);
    }

    @PostMapping("/getFormGDetails")
    public OIIPCRAResponse getFormGDetails(@RequestParam(value = "finYrId", required = false) Integer finYrId,
                                           @RequestParam(value = "issueNo", required = false) String issueNo,
                                           @RequestParam(value = "agencyId", required = false) Integer agencyId,
                                           @RequestParam(value = "userId", required = false) Integer userId) {
        return pdfService.getFormGDetails(finYrId,issueNo,agencyId, userId);
    }


    @PostMapping("/getAgencyDetailsByFinYr")
    public OIIPCRAResponse getAgencyDetailsByFinYr(@RequestParam(value = "finYrId", required = false) Integer finYrId)
    {
        return pdfService.getAgencyDetailsByFinYr(finYrId);
    }

    @PostMapping("/getBidderByBidIdDD")
    public OIIPCRAResponse getBidderByBidIdDD(@RequestParam(value = "bidId", required = false) Integer bidId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<BidderDetailsDto> bidderDto = pdfService.getBidderByBidIdDD(bidId);
                result.put("bidderDetails", bidderDto);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("Bidder Drop Down");
        } catch (Exception ex) {
            ex.printStackTrace();
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    ex.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/preBidMeetingResult")
    public void preBidMeetingResult(HttpServletResponse exportResponse,@RequestParam(value = "bidId",required = false) Integer bidId,
                                              @RequestParam(value = "bidOpeningDate",required = false)  java.sql.Date bidOpeningDate,
                                          @RequestParam(value = "userId",required = false)  Integer userId) throws IOException
    {
        try {
            Path file = Paths.get(pdfService.preBidMeetingResult(exportResponse,bidId,bidOpeningDate, userId).getAbsolutePath());
            if(Files.exists(file)){
                exportResponse.setContentType("application/pdf");
                exportResponse.addHeader("Content-Disposition", "attachment; filename"+ file.getFileName());
                Files.copy(file, exportResponse.getOutputStream());
                exportResponse.getOutputStream().flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/getPackageWiseBidder")
    public void getPackageWiseBidder(HttpServletResponse exportResponse,@RequestParam(value = "bidId",required = false) Integer bidId,
                                    @RequestParam(value = "bidOpeningDate",required = false)  java.sql.Date bidOpeningDate,
                                    @RequestParam(value = "packageId",required = false)Integer packageId,
                                    @RequestParam(value = "userId",required = false)  Integer userId) throws IOException
    {
        try {
            Path file = Paths.get(pdfService.getPackageWiseBidder(exportResponse,bidId,bidOpeningDate,packageId,userId).getAbsolutePath());
            if(Files.exists(file)){
                exportResponse.setContentType("application/pdf");
                exportResponse.addHeader("Content-Disposition", "attachment; filename"+ file.getFileName());
                Files.copy(file, exportResponse.getOutputStream());
                exportResponse.getOutputStream().flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/getFinancialBidPdf")
    public void getFinancialBidPdf(HttpServletResponse exportResponse,@RequestParam(value = "bidId",required = false) Integer bidId,
                                     @RequestParam(value = "technicalBidDate",required = false)  java.sql.Date technicalBidDate,
                                     @RequestParam(value = "packageId",required = false)Integer packageId,
                                     @RequestParam(value = "userId",required = false)  Integer userId) throws IOException
    {
        try {
            Path file = Paths.get(pdfService.getFinancialBidPdf(exportResponse,bidId,technicalBidDate,packageId,userId).getAbsolutePath());
            if(Files.exists(file)){
                exportResponse.setContentType("application/pdf");
                exportResponse.addHeader("Content-Disposition", "attachment; filename"+ file.getFileName());
                Files.copy(file, exportResponse.getOutputStream());
                exportResponse.getOutputStream().flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




}