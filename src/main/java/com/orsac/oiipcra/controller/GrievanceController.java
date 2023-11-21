package com.orsac.oiipcra.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orsac.oiipcra.bindings.OIIPCRAResponse;
import com.orsac.oiipcra.dto.*;
import com.orsac.oiipcra.entities.GrievanceEntity;
import com.orsac.oiipcra.service.AWSS3StorageService;
import com.orsac.oiipcra.service.GrievanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/grievance")
public class GrievanceController {

    @Autowired
    private GrievanceService grievanceService;

    @Autowired
    private AWSS3StorageService awss3StorageService;

    @Value("${accessGrievanceDocumentPath}")
    private String accessGrievanceDocumentPath;





    @PostMapping("/createGrievance")
    public OIIPCRAResponse createGrievance(@RequestParam String data,
                                           @RequestParam(name = "doc", required = false) MultipartFile doc,
                                           @RequestParam(name = "files", required = false) MultipartFile files) throws Exception{
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();

        try{
            GrievanceFileDto grievanceFileDto = objectMapper.readValue(data, GrievanceFileDto.class);
            if(files!=null) {
                grievanceFileDto.setImage(files.getOriginalFilename());
            }
            if(doc!=null) {
                grievanceFileDto.setDocument(doc.getOriginalFilename());
            }
            GrievanceEntity grievanceObj = grievanceService.createGrivance(grievanceFileDto);
            if(doc!=null) {
                boolean saveDocument = awss3StorageService.uploadGrievanceDocument(doc, String.valueOf(grievanceObj.getId()), doc.getOriginalFilename());
            }
            if(files!=null) {
                boolean saveImage = awss3StorageService.uploadGrievanceImage(files, String.valueOf(grievanceObj.getId()), files.getOriginalFilename());
            }
            result.put("grievance", grievanceObj);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.CREATED));
            response.setMessage("New Grievance Created");
        }
        catch (Exception e) {
            e.printStackTrace();
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;

    }


    @PostMapping ("/updateGrievance")
    public OIIPCRAResponse updateGrievance(@RequestBody GrievanceEntity grievanceEntity) throws Exception{
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();

        try{
            GrievanceEntity grievanceObj = grievanceService.updateGrivance(grievanceEntity);

            result.put("grievance", grievanceObj);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.CREATED));
            response.setMessage("Grievance Updated Successfully.");
        }
        catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;

    }

    @PostMapping("/getGrievanceById")
    public OIIPCRAResponse getGrievanceId(@RequestParam Integer id){
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            GrievanceDto grievanceDto = grievanceService.getGrievanceId(id);

            String documentPath = accessGrievanceDocumentPath+ id + "/" + grievanceDto.getDocument();
            String ImagePath = accessGrievanceDocumentPath+ id + "/" + grievanceDto.getImage();

            result.put("getGrievanceId", grievanceDto);
            result.put("grievanceDocument",documentPath);
            result.put("grievanceImage", ImagePath);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("Get Grievance by Id");
        }catch (Exception e){
            e.printStackTrace();
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }


    @PostMapping("/getAllGrievance")
    public OIIPCRAResponse getAllGrievance(@RequestBody GrievanceListingDto grievanceListingDto){
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {

            Page<GrievanceDto> grievanceListPage = grievanceService.getAllGrievance(grievanceListingDto);
            List<GrievanceDto> grievanceDtoList = grievanceListPage.getContent();
            if(!grievanceDtoList.isEmpty() && grievanceDtoList.size() > 0){
                result.put("grievanceDtoList",grievanceDtoList);
                result.put("currentPage", grievanceListPage.getNumber());
                result.put("totalItems", grievanceListPage.getTotalElements());
                result.put("totalPages", grievanceListPage.getTotalPages());
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            } else {
                result.put("grievanceDtoList", grievanceDtoList);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("Record not found.");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getGrievanceStatus")
    public OIIPCRAResponse getGrievanceStatus(){
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try{
            List<GrievanceStatusDto> grievanceStatusDtos = grievanceService.getGrievanceStatus();
            result.put("grievanceStatusId", grievanceStatusDtos);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("GrievanceStatus Id List");
            } catch (Exception e) {
                response = new OIIPCRAResponse(0,
                        new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                        e.getMessage(),
                        result);
            }
            return response;
        }

}
