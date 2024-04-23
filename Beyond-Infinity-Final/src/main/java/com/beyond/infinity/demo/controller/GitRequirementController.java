package com.beyond.infinity.demo.controller;

import com.beyond.infinity.demo.config.ConfigProperties;
import com.beyond.infinity.demo.model.conf.RequirementConfluenceProject;
import com.beyond.infinity.demo.model.git.RequirementGitProject;
import com.beyond.infinity.demo.model.jira.DuplicateRequirements;
import com.beyond.infinity.demo.model.jira.RequirementJiraEpics;
import com.beyond.infinity.demo.model.jira.RequirementJiraProject;
import com.beyond.infinity.demo.repository.cong.DesignDetailsRepository;
import com.beyond.infinity.demo.repository.git.GitDetailsRepository;
import com.beyond.infinity.demo.repository.jira.RequirementDetailsRepository;
import com.beyond.infinity.demo.repository.jira.RequirementDuplicateRepository;
import com.beyond.infinity.demo.request.GitRequest;
import com.beyond.infinity.demo.request.RequirementDuplicate;
import com.beyond.infinity.demo.response.jira.Epic;
import com.beyond.infinity.demo.response.jira.JiraEpicResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin("*")
public class GitRequirementController {

    @Autowired
    ConfigProperties properties;

    @Autowired
    private GitDetailsRepository gitDetailsRepository;

    @Autowired
    private DesignDetailsRepository designDetailsRepository;

    private RequirementGitProject requirementGitProject =new RequirementGitProject();
    private RequirementJiraEpics requirementJiraEpics;

    @RequestMapping(path="/git/{project}",method =RequestMethod.POST)
    public String getJiraEpicByProject( @PathVariable("project") String project,@RequestBody String gitUrl) throws IOException {
       /* SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000000);
        factory.setReadTimeout(3000000);*/
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {

            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });

        RestTemplate restTemplate=new RestTemplate();
        RequirementConfluenceProject domainProject =designDetailsRepository.findByJiraProjectIdIgnoreCase(project);

        GitRequest gitRequest=new GitRequest();
        gitRequest.setGitUrl(gitUrl);
        gitRequest.setProjectId(project);
        gitRequest.setDomain(domainProject.getDomain());
        HttpHeaders jiraHeaders=new HttpHeaders();
        // confHeaders.add("Authorization",cred);
        jiraHeaders.setContentType(MediaType.APPLICATION_JSON);


        HttpEntity<GitRequest> jiraRequestEntity=new HttpEntity<>(gitRequest, jiraHeaders );

        String serverUrl="http://localhost:8080/get-git-details";


        String result = restTemplate.postForObject(serverUrl, jiraRequestEntity, String.class);

        System.out.println(result);

        requirementGitProject.setProjectGitId(project);
        requirementGitProject.setGitUrl(gitUrl);
        requirementGitProject.setProjectGitLmSummary(result);


//        requirementJiraProject.setProjectLmSummary(result);
//
//
        gitDetailsRepository.save(requirementGitProject);

        return result;
    }



}
