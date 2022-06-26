package agents.harvester;

import java.util.List;

import model.harvester.Hit;
import model.harvester.InstrumentType;

public interface Harvester {
	public List<Hit> getHarvested(InstrumentType type);
}
