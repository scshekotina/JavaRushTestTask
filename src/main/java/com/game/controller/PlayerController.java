package com.game.controller;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.exception.PlayerNotFoundException;
import com.game.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class PlayerController {

    @Autowired
    private PlayerService playerService;

    @GetMapping("/rest/players/count")
    public ResponseEntity<Integer> countPlayers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Race race,
            @RequestParam(required = false) Profession profession,
            @RequestParam(required = false) Long after,
            @RequestParam(required = false) Long before,
            @RequestParam(required = false) Boolean banned,
            @RequestParam(required = false) Integer minExperience,
            @RequestParam(required = false) Integer maxExperience,
            @RequestParam(required = false) Integer minLevel,
            @RequestParam(required = false) Integer maxLevel) {
        Specification<Player> specification = playerService.getPlayerSpecification(name, title,
                race, profession, after, before, banned, minExperience, maxExperience,
                minLevel, maxLevel);
        return new ResponseEntity<Integer>(playerService.countPlayers(specification), HttpStatus.OK);
    }

    @GetMapping("/rest/players")
    public ResponseEntity<Iterable<Player>> getPlayers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Race race,
            @RequestParam(required = false) Profession profession,
            @RequestParam(required = false) Long after,
            @RequestParam(required = false) Long before,
            @RequestParam(required = false) Boolean banned,
            @RequestParam(required = false) Integer minExperience,
            @RequestParam(required = false) Integer maxExperience,
            @RequestParam(required = false) Integer minLevel,
            @RequestParam(required = false) Integer maxLevel,
            @RequestParam(required = false) PlayerOrder order,
            @RequestParam(required = false) Integer pageNumber,
            @RequestParam(required = false) Integer pageSize) {

        Specification<Player> specification = playerService.getPlayerSpecification(name, title,
                race, profession, after, before, banned, minExperience, maxExperience,
                minLevel, maxLevel);

        Pageable paging = playerService.getPageable(order, pageNumber, pageSize);

        return new ResponseEntity<>(
                playerService.getPlayers(specification, paging),
                HttpStatus.OK);
    }

    @PostMapping ("/rest/players")
    public ResponseEntity<Player> createPlayer(
            @RequestBody Player player) {

        if (player == null || !player.checkPlayerDataAllFilled()) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        player.fillCalculatedParams();

        return new ResponseEntity<>(
                playerService.save(player), HttpStatus.OK);
    }

    @GetMapping("/rest/players/{id}")
    public ResponseEntity<Player> getPlayer(@PathVariable Integer id) {
        if (id <= 0) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        Optional<Player> player = playerService.getPlayer(id);
        return player.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

    @PostMapping ("/rest/players/{id}")
    public ResponseEntity<Player> updatePlayer(@PathVariable Integer id,
            @RequestBody Player player) {
        if (id <= 0 || player == null || !player.checkPlayerDataOnlyFilled()) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        try {
            Player savedPlayer = playerService.update(id, player);
            return new ResponseEntity<>(savedPlayer, HttpStatus.OK);
        } catch (PlayerNotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping ("/rest/players/{id}")
    public ResponseEntity<Player> deletePlayer(@PathVariable Integer id) {
        if (id <= 0) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        try {
            playerService.checkAndDelete((long)id);
            return new ResponseEntity<>(null, HttpStatus.OK);
        } catch (PlayerNotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
}
