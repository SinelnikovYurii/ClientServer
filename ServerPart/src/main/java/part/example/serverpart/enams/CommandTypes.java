package part.example.serverpart.enams;

public enum CommandTypes {

    //USER COMMANDS
    LOGIN,
    CREATE_TOPIC,
    CREATE_VOTE,
    BASE_VIEW,
    EXTENDED_VIEW,
    VOTE_VIEW,
    VOTE,
    DELETE_VOTE,
    EXIT_USER,


    //SERVER COMMANDS
    LOAD,
    SAVE,
    EXIT_SERVER,

    UNSUPPORTED,


}
