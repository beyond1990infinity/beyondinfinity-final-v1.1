package com.beyond.infinity.demo.repository.git;


import com.beyond.infinity.demo.model.git.RequirementGitProject;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

@Repository
public interface GitDetailsRepository extends JpaRepository<RequirementGitProject, String> {

	RequirementGitProject findByProjectGitId(String jiraProjectId);

}
