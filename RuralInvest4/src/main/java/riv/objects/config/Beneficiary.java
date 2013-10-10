package riv.objects.config;

import javax.persistence.Entity;
import javax.persistence.DiscriminatorValue;

/**
 * @author Bar Zecharya
 * A beneficiary.  Projects created in RuralInvest can be associated to one of these.
 */
@Entity
@DiscriminatorValue("1")
public class Beneficiary extends AppConfig {
	private static final long serialVersionUID = 447020925627089268L;

	/** default constructor */
    public Beneficiary() {
    }
    
    
}