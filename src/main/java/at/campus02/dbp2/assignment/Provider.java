package at.campus02.dbp2.assignment;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
@Entity
public class Provider {
    @Id @GeneratedValue
    private Integer id;
    private ProviderType type;
    private String address;
    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Appointment> appointments = new ArrayList<>();

    public Integer getId() {
        return id;
    }

    public ProviderType getType() {
        return type;
    }

    public void setType(ProviderType type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Provider provider = (Provider) o;
        return Objects.equals(getId(), provider.getId()) && getType() == provider.getType() && Objects.equals(getAddress(), provider.getAddress());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getType(), getAddress());
    }

}
