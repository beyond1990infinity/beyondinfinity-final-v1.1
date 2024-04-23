package com.beyond.infinity.demo.controller;

import com.beyond.infinity.demo.config.ConfigProperties;
import com.beyond.infinity.demo.model.conf.RequirementConfluenceProject;
import com.beyond.infinity.demo.model.git.RequirementGitProject;
import com.beyond.infinity.demo.model.jira.DuplicateRequirements;
import com.beyond.infinity.demo.model.jira.RequirementJiraProject;
import com.beyond.infinity.demo.repository.cong.DesignDetailsRepository;
import com.beyond.infinity.demo.repository.git.GitDetailsRepository;
import com.beyond.infinity.demo.request.RequirementDuplicate;
import com.beyond.infinity.demo.response.conf.ConfluencePageResponse;
import com.beyond.infinity.demo.response.conf.ProjectDetails;

import com.beyond.infinity.demo.response.jira.JiraEpicResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin("*")
public class ConfluenceDesignController {

    @Autowired
    ConfigProperties properties;

    @Autowired
    private DesignDetailsRepository designDetailsRepository;
    @Autowired
    private GitDetailsRepository gitDetailRepository;

    private RequirementConfluenceProject requirementConfluenceProject = new RequirementConfluenceProject();

    @RequestMapping(path="/confluence/title/{title}",method =RequestMethod.POST)
    public ConfluencePageResponse getConfluence(@RequestHeader("Authorization") final String cred, @PathVariable("title") String title ) throws IOException {
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {

            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
        RestTemplate restTemplate=new RestTemplate();

        HttpHeaders headers=new HttpHeaders();
        headers.add("Authorization",cred);
        headers.setAccept(MediaType.parseMediaTypes("application/json"));

        String serverUrl=null;

        String host=this.properties.confluence().getHost();
        String endpoint=this.properties.confluence().getGetdata_endpoint();

        HttpEntity<String> requestEntity=new HttpEntity<>(headers);

        serverUrl=host+endpoint+title+"&expand=body.view,version";

        ResponseEntity<ConfluencePageResponse> response=restTemplate.exchange(serverUrl, HttpMethod.GET,requestEntity,new ParameterizedTypeReference<ConfluencePageResponse>(){});
        ConfluencePageResponse confluencePageResponse=response.getBody();

        Document doc = Jsoup.parse(confluencePageResponse.getPageDetails().get(0).getBody().getView().getValue());

        String content = doc.getElementsByTag("p").text();

        confluencePageResponse.getPageDetails().get(0).getBody().getView().setValue(content);



        System.out.println(content);
        requirementConfluenceProject.setProjectId("TEAM");
        requirementConfluenceProject.setTitle( confluencePageResponse.getPageDetails().get(0).getTitle());
        requirementConfluenceProject.setDescription(content);
        requirementConfluenceProject.setJiraProjectId("TYTR");


        HttpHeaders confHeaders=new HttpHeaders();
       // confHeaders.add("Authorization",cred);
        confHeaders.setContentType(MediaType.APPLICATION_JSON);


        HttpEntity<ConfluencePageResponse> confRequestEntity=new HttpEntity<>(confluencePageResponse, confHeaders );

        serverUrl="http://localhost:8080/get-brd-summary";

        String result = restTemplate.postForObject(serverUrl,confRequestEntity, String.class);

        System.out.println(result);


        requirementConfluenceProject.setLlmConfSummary(result);

        requirementConfluenceProject.setDomain("Food,Order");

        designDetailsRepository.save(requirementConfluenceProject);

        return confluencePageResponse;//response.getBody();
    }


    @RequestMapping(path="/confluence/project/{projectId}",method=RequestMethod.PUT)
    public ResponseEntity<Integer> updateJiraProjectWithLMSummary(@RequestHeader("Authorization") final String cred, @PathVariable("projectId") String projectId, @RequestBody RequirementConfluenceProject reqDetails){
        RequirementConfluenceProject projectDetails=designDetailsRepository.findByProjectIdIgnoreCase(projectId);

        if(projectDetails.getProjectId()!=null) {
            requirementConfluenceProject.setLlmConfSummary(reqDetails.getLlmConfSummary());
            return new ResponseEntity<>(designDetailsRepository.updateProjectSummary(projectId, reqDetails.getLlmConfSummary()), HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    @RequestMapping(path="/confluence/getAll",method=RequestMethod.GET)
    public List<ProjectDetails> getAll() throws IOException {
    	
    	Random random = new Random();
    	int x = random.nextInt(900) + 100;
    	List<ProjectDetails> projectList=new ArrayList<>();
    	List<RequirementConfluenceProject> allprojectDetails=designDetailsRepository.findAll();
        if(!allprojectDetails.isEmpty()) {
            for(RequirementConfluenceProject cp:allprojectDetails) {
            	ProjectDetails project=new ProjectDetails();
            	project.setId(cp.getId());
            	project.setAppName(cp.getTitle());
            	project.setConfluenceTitle(cp.getTitle());
            	project.setEim(cp.getJiraProjectId()+x);
            	project.setJiraProjectId(cp.getJiraProjectId());
            	project.setDescription(cp.getLlmConfSummary());
            	RequirementGitProject gitProject=gitDetailRepository.findByProjectGitId(cp.getJiraProjectId());
            	if(gitProject!=null) {
            	project.setGitUrl(gitProject.getGitUrl());}
            	projectList.add(project);
            	
            }
            return projectList;
        }
        else
        {
        	return projectList;
        }
		
		
        
    }

}
