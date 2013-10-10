package riv.objects.config;

import javax.persistence.Entity;
import javax.persistence.DiscriminatorValue;

/**
 * @author Bar Zecharya
 * An environmental category.  Projects created in RuralInvest can be associated to one of these.
 */
@Entity
@DiscriminatorValue("3")
public class EnviroCategory extends AppConfig {
	private static final long serialVersionUID = -8016286550435050674L;

	/** default constructor */
    public EnviroCategory() {
    }
}