package com.example.dtroguelike.infrastructure.data;

/**
 * DTO plano que refleja la forma de cada entrada en clubs.json.
 * Se mapea a {@link com.example.dtroguelike.domain.club.Club} en
 * {@link ClubDataLoader}.
 */
public class ClubDto {
    public String id;
    public String name;
    public String country;
    public String leagueId;
    public int reputation;
    public int attack;
    public int midfield;
    public int defense;
}
