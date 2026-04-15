package fr.augustinbaffou.unseen.bar.service.osm.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OverpassResponse(List<OverpassElement> elements) {}
