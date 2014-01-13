package lorenz.lab10;

import lorenz.lab10.PropertySheet.FieldValidator;

/**
 * 
 * @author jehanson
 */
public class RotatingPendulumScenario extends ODETrajectoryPairScenario {

	// ========================================
	// Creation
	// ========================================

	public RotatingPendulumScenario() {
		super(new RotatingPendulumSystem());
	}

	// ========================================
	// Operation
	// ========================================

	@Override
	protected PropertySheet.FieldValidator getCoefficientValidator(String coefficientLabel) {
		if (coefficientLabel == null)
			throw new IllegalArgumentException(
					"Argument \"coefficientLabel\" cannot be null");
		else if (coefficientLabel.equals(RotatingPendulumSystem.G_LABEL))
			return PropertySheet.DoubleValidator.any();
		else if (coefficientLabel.equals(RotatingPendulumSystem.L_LABEL))
			return PropertySheet.DoubleValidator.greaterThan(0.);
		else if (coefficientLabel.equals(RotatingPendulumSystem.LAMBDA_LABEL))
			return PropertySheet.DoubleValidator.any();
		else
			throw new IllegalArgumentException("Coefficient label \"" + coefficientLabel
					+ "\" not recognized.");
	}

	@Override
	protected FieldValidator getCoordinateValidator(String coordinateLabel) {
		if (coordinateLabel == null)
			throw new IllegalArgumentException(
					"Argument \"coordinateLabel\" cannot be null");
		else if (coordinateLabel.equals(RotatingPendulumSystem.THETA_LABEL))
			return new PropertySheet.DoubleValidator(0, true, Math.PI, false);
		else if (coordinateLabel.equals(RotatingPendulumSystem.PHI_LABEL))
			return new PropertySheet.DoubleValidator(0, true, 2*Math.PI, false);
		else if (coordinateLabel.equals(RotatingPendulumSystem.THETA_DOT_LABEL))
			return PropertySheet.DoubleValidator.any();
		else
			throw new IllegalArgumentException("Coordinate label \"" + coordinateLabel
					+ "\" not recognized");
	}
}
