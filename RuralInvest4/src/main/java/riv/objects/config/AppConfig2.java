package riv.objects.config;

import javax.persistence.Entity;
import javax.persistence.DiscriminatorValue;

@Entity
@DiscriminatorValue("6")
public class AppConfig2 extends AppConfig {
	private static final long serialVersionUID = 8355199796799527553L;

	/** default constructor */
    public AppConfig2() {
    }
}