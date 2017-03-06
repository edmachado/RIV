package riv.objects.config;

import javax.persistence.Entity;
import javax.persistence.DiscriminatorValue;

/**
 * A field office.  Each Profile and Project must be associated to one of these.
 * @author Bar Zecharya
 *
 */
@Entity
@DiscriminatorValue("0")
public class FieldOffice  extends AppConfig {
	private static final long serialVersionUID = 6546887041530033726L;

	/**
     * Default constructor
     */
    public FieldOffice() {
    }
}