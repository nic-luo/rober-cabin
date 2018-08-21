package group.rober.office.word.parameter;


/**
 * 金额型参数
 *
 * @author yangsong
 */
public class MoneyParameter extends NumberParameter<Double> {
	private static final long serialVersionUID = 3467532420829654187L;
	
	public static final String NUMBER_FORMAT = "###,###,###,###,###,###,###,###,###,##0.00";

	public MoneyParameter(String name, Double value) {
		super(NUMBER_FORMAT, name, value);
	}

}
