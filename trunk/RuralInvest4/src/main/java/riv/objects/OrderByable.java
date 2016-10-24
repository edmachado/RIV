/**
 * 
 */
package riv.objects;

/**
 * @author Bar Zecharya
 *
 */
public interface OrderByable extends Comparable<OrderByable> {
	Integer getOrderBy();
	void setOrderBy(Integer i);
}
