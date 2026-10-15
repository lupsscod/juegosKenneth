package main;

public class Jugador {
    private String nickname;

    public Jugador(String nicknameDefecto) {
        this.nickname = nicknameDefecto;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}