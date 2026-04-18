package com.accenture.co.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.accenture.co.domain.model.Branch;


@Repository
public interface BranchRespository extends ReactiveCrudRepository<Branch, Long> {


}
