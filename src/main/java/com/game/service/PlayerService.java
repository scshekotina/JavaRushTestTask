package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.exception.PlayerNotFoundException;
import com.game.repository.PlayerRepository;
import com.game.repository.PlayerSpecificationsBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
public class PlayerService {

    private static final int DEFAULT_PAGE_NUMBER = 0;
    private static final int DEFAULT_PAGE_SIZE = 3;

    @Autowired
    private PlayerRepository playerRepository;

    public Iterable<Player> getPlayers(Specification<Player> specification, Pageable limit) {
        return playerRepository.findAll(specification, limit).getContent();
    }

    public int countPlayers(Specification<Player> specification) {
        return (int)playerRepository.count(specification);
    }

    public Player save(Player player) {
        return playerRepository.save(player);
    }

    public Optional<Player> getPlayer(Integer id) {
        return playerRepository.findById(id.longValue());
    }

    @Transactional
    public void checkAndDelete(Long id) throws PlayerNotFoundException {
        if (playerRepository.existsById(id)) {
            playerRepository.deleteById(id);
        }
        else {
            throw new PlayerNotFoundException();
        }
    }

    @Transactional
    public Player update(Integer id, Player newPlayerData) throws PlayerNotFoundException {
        Optional<Player> playerFromDB = getPlayer(id);
        if (!playerFromDB.isPresent()) {
            throw new PlayerNotFoundException();
        }
        Player playerForSave = playerFromDB.get();
        if (!newPlayerData.containsNotNullField()) {
            return playerForSave;
        }
        playerForSave.update(newPlayerData);
        return save(playerForSave);
    }

    public Specification<Player> getPlayerSpecification(String name, String title, Race race, Profession profession, Long after, Long before, Boolean banned, Integer minExperience, Integer maxExperience, Integer minLevel, Integer maxLevel) {
        PlayerSpecificationsBuilder builder = new PlayerSpecificationsBuilder();
        builder = builder.with("name", ":", name)
                .with("title", ":", title)
                .with("race", ":", race)
                .with("banned", ":", banned)
                .with("profession", ":", profession)
                .with("experience", ">=", minExperience)
                .with("experience", "<=", maxExperience)
                .with("level", ">=", minLevel)
                .with("level", "<=", maxLevel);

        if (after != null) {
            builder = builder.with("birthday", ">=", new Date(after));
        }
        if (before != null) {
            builder = builder.with("birthday", "<=", new Date(before));
        }

        return builder.build();
    }

    public Pageable getPageable(PlayerOrder order, Integer pageNumber, Integer pageSize) {
        return PageRequest.of(pageNumber != null ? (int) pageNumber : DEFAULT_PAGE_NUMBER,
                pageSize != null ? (int) pageSize : DEFAULT_PAGE_SIZE,
                order != null ? Sort.by(order.getFieldName()) : Sort.by(PlayerOrder.ID.getFieldName()));
    }
}
