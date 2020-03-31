package com.checkmarx.flow.dto.report;

import com.checkmarx.flow.dto.ScanRequest;
import com.checkmarx.flow.dto.Status;
import com.checkmarx.flow.utils.AesEncryptionUtils;
import com.checkmarx.sdk.exception.CheckmarxException;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;

import lombok.Data;

import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static net.logstash.logback.marker.Markers.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public abstract class AnalyticsReport {

    protected static final Logger jsonlogger = LoggerFactory.getLogger("jsonLogger");
    protected static final Logger log = org.slf4j.LoggerFactory.getLogger(AnalyticsReport.class);

    protected static final String NOT_APPLICABLE = "NA";
    public static final String SAST = "SAST";
    public static final String OSA = "OSA";
    
    protected String projectName = NOT_APPLICABLE;
    protected String repoUrl = null;
    protected String scanInitiator;
    protected String scanId;
    
    
    public AnalyticsReport(String scanId, ScanRequest request) {
        this.scanId = scanId;
        scanInitiator = OSA;
        if(scanId==null){
            this.scanId = NOT_APPLICABLE;
        }
        this.projectName = request.getProject();
    }

    public AnalyticsReport(Integer scanId, ScanRequest request) {
        if(scanId!=null) {
            this.scanId = scanId.toString();
        }else{
            this.scanId = NOT_APPLICABLE;
        }
        this.projectName = request.getProject();
        scanInitiator = SAST;
    }
    
    public void log() {
        jsonlogger.info(append(_getOperation(), this), "");
    }

    //adding underscore to prevent getOperation() to be called during logging of this object in log()
    //since we don't want the OPERATION to be a part of the logged object
    protected abstract String _getOperation();

    
    protected void setEncodedRepoUrl(String sourcesPath) {
        try {
            if(sourcesPath != null) {
                repoUrl = sourcesPath;
                this.repoUrl = AesEncryptionUtils.encrypt(repoUrl);
            }else{
                repoUrl = NOT_APPLICABLE;
            }
            
        } catch (CheckmarxException e) {
            this.repoUrl = NOT_APPLICABLE;
            
            log.error( "Unable to encrypt repoUrl " + e.getMessage());
        }
    }
}
