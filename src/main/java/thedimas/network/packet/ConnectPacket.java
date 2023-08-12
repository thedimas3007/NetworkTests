package thedimas.network.packet;

/** Basic example packet based on {@link Packet} interface */
public class ConnectPacket implements Packet {
    private String name;
    private String lang;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    @Override
    public String toString() {
        return "ConnectPacket{" +
                "name='" + name + '\'' +
                ", lang='" + lang + '\'' +
                '}';
    }
}
