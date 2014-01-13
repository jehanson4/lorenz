package lorenz.lab10;

import lorenz.lab10.PropertySheet.FieldValidator;

/**
 * 
 * @author jehanson
 */
public class LorenzScenario extends ODETrajectoryPairScenario {

	// ========================================
	// Creation
	// ========================================

	public LorenzScenario() {
		super(new LorenzSystem());
	}

	// ========================================
	// Operation
	// ========================================

	@Override
	protected PropertySheet.FieldValidator getCoefficientValidator(String coeffientLabel) {
		if (coeffientLabel == null)
			throw new IllegalArgumentException(
					"Argument \"coefficientLabel\" cannot be null");
		else if (coeffientLabel.equals(LorenzSystem.SIGMA_LABEL))
			return PropertySheet.DoubleValidator.any();
		else if (coeffientLabel.equals(LorenzSystem.RHO_LABEL))
			return PropertySheet.DoubleValidator.any();
		else if (coeffientLabel.equals(LorenzSystem.BETA_LABEL))
			return PropertySheet.DoubleValidator.any();
		else
			throw new IllegalArgumentException("Coefficient \"" + coeffientLabel
					+ "\" not recognized.");
	}

	@Override
	protected FieldValidator getCoordinateValidator(String coordinateLabel) {
		if (coordinateLabel == null)
			throw new IllegalArgumentException(
					"Argument \"coordinateLabel\" cannot be null");
		else if (coordinateLabel.equals(LorenzSystem.X_LABEL))
			return PropertySheet.DoubleValidator.any();
		else if (coordinateLabel.equals(LorenzSystem.Y_LABEL))
			return PropertySheet.DoubleValidator.any();
		else if (coordinateLabel.equals(LorenzSystem.Z_LABEL))
			return PropertySheet.DoubleValidator.any();
		else
			throw new IllegalArgumentException("Coordinate label \"" + coordinateLabel
					+ "\" not recognized");
	}
}
