package fr.augustinbaffou.unseen.bar.service;

import fr.augustinbaffou.unseen.bar.entity.Bar;
import fr.augustinbaffou.unseen.bar.repository.BarRepository;
import fr.augustinbaffou.unseen.bar.service.osm.OsmBarMapper;
import fr.augustinbaffou.unseen.bar.service.osm.dto.OverpassElement;
import fr.augustinbaffou.unseen.bar.service.osm.dto.OverpassResponse;
import fr.augustinbaffou.unseen.commun.exception.ResourceAlreadyExistsException;
import fr.augustinbaffou.unseen.commun.exception.ResourceNotFoundException;
import fr.augustinbaffou.unseen.commun.service.BaseService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import static fr.augustinbaffou.unseen.bar.controller.navigation.BarExceptionConstants.*;

@Service
public class BarImportByOsmIdService extends BaseService<String, Bar> {

    private static final String OVERPASS_BASE_URL = "https://overpass-api.de/api";
    private static final String OVERPASS_QUERY_TEMPLATE =
            "[out:json];(node(%s);way(%s););out center body;";

    private final RestClient restClient;
    private final BarRepository barRepository;
    private final OsmBarMapper osmBarMapper;

    public BarImportByOsmIdService(RestClient.Builder restClientBuilder, BarRepository barRepository, OsmBarMapper osmBarMapper) {
        this.restClient    = restClientBuilder.baseUrl(OVERPASS_BASE_URL).build();
        this.barRepository = barRepository;
        this.osmBarMapper  = osmBarMapper;
    }

    /**
     * @param osmId format "node/123456789" ou "way/123456789"
     */
    @Override
    public Bar execute(String osmId) {
        if (barRepository.existsByOsmId(osmId)) {
            throw new ResourceAlreadyExistsException(RESOURCE_NAME, FIELD_OSM_ID, osmId);
        }

        String[] parts = osmId.split("/", 2);
        String type      = parts[0];
        String numericId = parts[1];

        String query = OVERPASS_QUERY_TEMPLATE.formatted(numericId, numericId);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("data", query);

        OverpassResponse response = restClient.post()
                .uri("/interpreter")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(formData)
                .retrieve()
                .body(OverpassResponse.class);

        OverpassElement element = findElement(response, type, numericId, osmId);
        Bar bar = osmBarMapper.toBar(osmId, element);
        return barRepository.save(bar);
    }

    private OverpassElement findElement(OverpassResponse response, String type, String numericId, String osmId) {
        if (response == null || response.elements() == null) {
            throw new ResourceNotFoundException(OSM_RESOURCE_NAME, FIELD_OSM_ID, osmId);
        }
        return response.elements().stream()
                .filter(e -> type.equals(e.type()) && String.valueOf(e.id()).equals(numericId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(OSM_RESOURCE_NAME, FIELD_OSM_ID, osmId));
    }
}
