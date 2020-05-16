package com.cscie599.gfn.controller;


import com.cscie599.gfn.entities.Publication;
import com.cscie599.gfn.entities.PublicationAuthor;
import com.cscie599.gfn.repository.PublicationRepository;
import com.cscie599.gfn.views.PublicationView;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping("/api")
public class PublicationController {

    protected static final Log logger = LogFactory.getLog(PublicationController.class);

    // List of ortholog taxIds to be used for fetching publications.
    private List<Integer> taxIdsToProcess;

    @Autowired
    PublicationRepository publicationRepository;

    /**
     * Retrieves all publications for a gene-MeSH term combination.
     *
     * @param body Request body containing a gene ID and a MeSH term ID
     * @return ResponseEntity containing publications or error
     */
    @PostMapping("/publications")
    public ResponseEntity<?> getAllPublications(@RequestBody Map<String, Object> body) {
        LinkedHashMap<String, Object> response = validate(body);
        if (response.containsKey("error")) {
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }
        String geneId = body.get("geneId").toString();
        String meshId = body.get("meshId").toString();
        List<PublicationView> publicationViews = new ArrayList<>();
        List<Publication> publications = publicationRepository.findByGeneIdAndMeshId(geneId, meshId, taxIdsToProcess);
        for (Publication publication : publications) {
            Collection<PublicationAuthor> publicationAuthors = publication.getPublicationAuthorCollection();
            List<HashMap> authors = new ArrayList<>();
            for (PublicationAuthor publicationAuthor : publicationAuthors) {
                HashMap<String, String> author = new HashMap<String, String>();
                author.put("foreName", publicationAuthor.getAuthor().getForeName());
                author.put("lastName", publicationAuthor.getAuthor().getLastName());
                authors.add(author);
            }
            publicationViews.add(new PublicationView(
                    publication.getPublicationId().trim(),
                    publication.getCompletedDate(),
                    publication.getDateRevised(),
                    publication.getTitle(),
                    authors));
        }
        response.put("geneId", geneId);
        response.put("meshId", meshId);
        response.put("results", publicationViews);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Value("classpath:filtered_taxids.properties")
    public void setResourceFile(Resource resourceFile) {
        taxIdsToProcess = new ArrayList<>(40);
        if (resourceFile.exists()) {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(resourceFile.getInputStream(), StandardCharsets.UTF_8.name()));
                String line = br.readLine();
                while (line != null) {
                    taxIdsToProcess.add(Integer.parseInt(line.trim()));
                    line = br.readLine();
                }
            } catch (IOException e) {
                logger.error("Unable to read the file with publications to be skipped");
            }
        }
        logger.info("taxIdsToProcess has been initialized with " + taxIdsToProcess);
    }

    /**
     * Validates the body request for getAllPublications.
     *
     * @param body Request body
     * @return Response containing error if any
     */
    private LinkedHashMap<String, Object> validate(Map<String, Object> body) {
        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        if (!(body.containsKey("geneId"))) {
            response.put("error", "Missing gene ID.");
        } else if (!(body.containsKey("meshId"))) {
            response.put("error", "Missing mesh term ID.");
        } else if (StringUtils.isBlank(body.get("geneId").toString())) {
            response.put("error", "Gene ID cannot be blank.");
        } else if (StringUtils.isBlank(body.get("meshId").toString())) {
            response.put("error", "Mesh term ID cannot be blank.");
        }
        return response;
    }

}
