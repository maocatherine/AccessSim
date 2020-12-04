package edu.kit.ifv.mobitopp.visum;


import java.awt.geom.Area;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class VisumTerritory 
	implements Serializable
{

	private static final long serialVersionUID = 1L;

	public final int id;
	public final String code;
	public final String name;

	public final int surfaceId;

	protected	VisumSurface surface;

	public VisumTerritory(
		int id,
		String code,
		String name,
		int surfaceId,
		VisumSurface surface
	) {
		this.id = id;
		this.code = code;
		this.name = name;
		this.surfaceId = surfaceId;
		this.surface = surface;
	}
	

	public List<Area> areas() {
		return surface.areas();
	}

	public Area intersect(Area area) {

		Area result = new Area();

		for(Area current : surface.areas()) {

			if (current.intersects(area.getBounds2D())) {

				Area tmp = new Area(area);
				tmp.intersect(current);

				result.add(tmp);
			}
		}

		return result;
	}

  @Override
  public int hashCode() {
    return Objects.hash(code, id, name, surface, surfaceId);
  }


  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    VisumTerritory other = (VisumTerritory) obj;
    return Objects.equals(code, other.code) && id == other.id && Objects.equals(name, other.name)
        && Objects.equals(surface, other.surface) && surfaceId == other.surfaceId;
  }


  public String toString() {
    return "VisumTerritory(" + id + "," + code + "," + name + "," + surfaceId + "," + surface + ")";
  }

}
