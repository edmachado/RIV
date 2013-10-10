package riv.objects.config;

import javax.persistence.Entity;
import javax.persistence.DiscriminatorValue;

/**
 * @author Bar Zecharya
 * A project or profile status.  Projects and profiles created in RuralInvest can be associated to one of these.
 */
@Entity
@DiscriminatorValue("4")
public class Status extends AppConfig {
	private static final long serialVersionUID = 8576506514842157053L;

	/** default constructor */
    public Status() {
    }
}