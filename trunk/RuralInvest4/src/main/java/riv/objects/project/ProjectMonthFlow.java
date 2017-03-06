package riv.objects.project;

public class ProjectMonthFlow {
	private String description;
	private double[] flowData;
	
	public ProjectMonthFlow() {
		setFlowData(new double[12]);
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setFlowData(double[] flowData) {
		this.flowData = flowData;
	}

	public double[] getFlowData() {
		return flowData;
	}
	

}
