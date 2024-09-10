package calabresoCorp.steamData.controller;

import calabresoCorp.steamData.model.GameInfo;
import calabresoCorp.steamData.model.PlayerCount;
import calabresoCorp.steamData.service.SteamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class SteamAPIController {

    @Autowired
    private SteamService steamService;

    @GetMapping("/buscar-jogo")
    public GameInfo buscarJogo(@RequestParam String nome) {
        try {
            return steamService.buscarJogoPorNome(nome);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    @GetMapping("/jogadores-online")
    public PlayerCount getJogadoresOnline(@RequestParam int appId) {
        try {
            return steamService.getPlayerCount(appId);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
