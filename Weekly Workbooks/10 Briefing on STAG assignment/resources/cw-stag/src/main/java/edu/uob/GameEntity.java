package edu.uob;

public abstract class GameEntity
{
    // entity names cannot contain spaces
    // entity names defined in the configuration files will be unique
    //  there should only be a single instance of each entity within the game
    private final String name;
    private final String description;

    public GameEntity(String name, String description)
    {
        this.name = name;
        this.description = description;
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }
}
