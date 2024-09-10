package calabresoCorp.steamData.service;

import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import calabresoCorp.steamData.model.GameInfo;
import calabresoCorp.steamData.model.PlayerCount;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;


@Service
public class SteamService {
    private static final String API_KEY = "17E3199D634564CAA69759598A60F9CB";
    private static final String STEAM_APP_LIST_URL = "https://api.steampowered.com/ISteamApps/GetAppList/v2/";
    private static final String STEAM_APP_DETAILS_URL = "https://store.steampowered.com/api/appdetails?appids=";
    private static final String STEAM_PLAYER_COUNT_URL = "https://api.steampowered.com/ISteamUserStats/GetNumberOfCurrentPlayers/v1/?appid=";
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public GameInfo buscarJogoPorNome(String nome) throws IOException, InterruptedException {
        int appId = buscarAppIdPorNome(nome);

        if (appId == -1) {
            return null;
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(STEAM_APP_DETAILS_URL + appId))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonNode gameDetails = objectMapper.readTree(response.body()).get(String.valueOf(appId)).get("data");

        if (gameDetails == null) {
            return null;
        }

        GameInfo gameInfo = new GameInfo();
        gameInfo.setName(gameDetails.get("name").asText());
        gameInfo.setDescription(gameDetails.get("short_description").asText());
        gameInfo.setDevelopers(objectMapper.convertValue(gameDetails.get("developers"), List.class));
        gameInfo.setPublishers(objectMapper.convertValue(gameDetails.get("publishers"), List.class));
        gameInfo.setPrice(gameDetails.has("price_overview") ? gameDetails.get("price_overview").get("final_formatted").asText() : "Gratuito");

        return gameInfo;
    }

    public PlayerCount getPlayerCount(int appId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(STEAM_PLAYER_COUNT_URL + appId))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonNode playerData = objectMapper.readTree(response.body()).get("response");

        PlayerCount playerCount = new PlayerCount();
        playerCount.setPlayerCount(playerData.get("player_count").asInt());

        return playerCount;
    }

    private int buscarAppIdPorNome(String nome) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(STEAM_APP_LIST_URL))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonNode jsonNodeAppList = objectMapper.readTree(response.body());

        JsonNode jogos = jsonNodeAppList.get("applist").get("apps");

        for (JsonNode jogo : jogos) {
            if (jogo.get("name").asText().equalsIgnoreCase(nome)) {
                return jogo.get("appid").asInt();
            }
        }

        return -1;
    }
}
