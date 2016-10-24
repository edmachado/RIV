package riv.objects.config;

import javax.persistence.Entity;
import javax.persistence.DiscriminatorValue;

@Entity
@DiscriminatorValue("5")
public class AppConfig1 extends AppConfig {
	private static final long serialVersionUID = 7272317060187384494L;

	/** default constructor */
    public AppConfig1() {
    }
}