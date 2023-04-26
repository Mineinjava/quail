package quail.util;

/**
 * Represents a two-dimensional vector.
 * 
 * @author Alex Green
 */
public class Vec2d
{
	public final double x;
	public final double y;
	
	/**
	 * Creates a unit vector pointing in the positive x direction.
	 */
	public Vec2d()
	{
		this(1d, 0d);
	}
	
	/**
	 * Creates a vector pointing in the positive x direction with the specified magnitude.
	 * 
	 * @param length - the length of the vector
	 */
	public Vec2d(double length)
	{
		this(length, 0d);
	}
	
	/**
	 * Creates a vector from x and y coordinates.
	 * 
	 * @param xIn - the x coordinate
	 * @param yIn - the y coordinate
	 */
	public Vec2d(double xIn, double yIn)
	{
		if(xIn == -0d || Double.isNaN(xIn))
		{
			xIn = 0d;
		}
		if(yIn == -0d || Double.isNaN(yIn))
		{
			yIn = 0d;
		}
		
		this.x = xIn;
		this.y = yIn;
	}
	
	/**
	 * Creates a unit vector from a rotation.
	 * 
	 * @param rotation - the angle of rotation (positive x is 0°)
	 * @param isDegrees - whether or not the passed rotation is in degrees or radians
	 */
	public Vec2d(double rotation, boolean isDegrees)
	{
		this(rotation, 1d, isDegrees);
	}
	
	/**
	 * Creates a vector from a rotation with a specified magnitude.
	 * 
	 * @param rotation - the angle of rotation (positive x is 0°)
	 * @param length - the length of the vector
	 * @param isDegrees - whether or not the passed rotation is in degrees or radians
	 */
	public Vec2d(double rotation, double length, boolean isDegrees)
	{
		if(isDegrees)
		{
			rotation *= Math.PI / 180d;
		}
		
		this.x = Math.cos(rotation) * length;
		this.y = Math.sin(rotation) * length;
	}

	/**
	 * Creates a vector from an array of coordinates.
	 *
	 * @param coords - the array of coordinates
	 */
	public Vec2d(double[] coords)
	{
		this(coords[0], coords[1]);
	}
	
	/**
	 * Normalizes the vector.
	 * 
	 * @return A normalized version of this vector.
	 */
	public Vec2d normalize()
	{
		double scale = Math.sqrt(this.x * this.x + this.y * this.y);
		return new Vec2d(this.x / scale, this.y / scale);
	}
	
	/**
	 * Scales the vector.
	 * 
	 * @param scale
	 * @return A scaled version of the vector
	 */
	public Vec2d scale(double scale)
	{
		return new Vec2d(this.x * scale, this.y * scale);
	}
	
	/**
	 * Calculates the length of the vector by using the pythagorean theorem.
	 * 
	 * @return The vector's length.
	 */
	public double getLength()
	{
		return Math.sqrt(this.x * this.x + this.y * this.y);
	}
	
	/**
	 * Calculates the squared length of the vector. Faster than {@link #getLength() getLength()}
	 * 
	 * @return The squared length of the vector.
	 */
	public double getLengthSquared()
	{
		return this.x * this.x + this.y * this.y;
	}
	
	/**
	 * Adds the vector to the passed in vector.
	 * 
	 * @param vec - the vector to add
	 * @return The resulting vector after the addition.
	 */
	public Vec2d add(Vec2d vec)
	{
		return this.add(vec.x, vec.y);
	}
	
	/**
	 * Adds the vector to the passed in vector.
	 * 
	 * @param vecX - the vector's x coordinate
	 * @param vecY - the vector's y coordinate
	 * @return The resulting vector after the addition.
	 */
	public Vec2d add(double vecX, double vecY)
	{
		return new Vec2d(this.x + vecX, this.y + vecY);
	}
	
	/**
	 * Subtracts the vector by the passed in vector.
	 * 
	 * @param vec - the vector to subtract
	 * @return The resulting vector after the subtraction.
	 */
	public Vec2d subtract(Vec2d vec)
	{
		return this.add(-vec.x, -vec.y);
	}
	
	/**
	 * Subtracts the vector by the passed in vector.
	 * 
	 * @param vecX - the vector's x coordinate
	 * @param vecY - the vector's y coordinate
	 * @return The resulting vector after the subtraction.
	 */
	public Vec2d subtract(double vecX, double vecY)
	{
		return this.add(-vecX, -vecY);
	}
	/**
	 * Calculates the vector's rotation, with 0° being the positive x direction.
	 * 
	 * @return The vector's rotation in radians.
	 */
	public double getAngle()
	{
		double a = Math.acos(this.x / this.getLength());
		return this.y < 0d ? 2d * Math.PI - a : a;
	}
	
	/**
	 * Calculates the smallest angle to the passed vector.
	 * 
	 * @param vec - the vector to compare to
	 * @return The smallest angular distance between the two vectors.
	 */
	public double distanceTo(Vec2d vec)
	{
		double l1 = this.getLength();
		double l2 = vec.getLength();
		double d = this.dot(vec);
		
		return Math.acos(d / (l1 * l2));
	}
	
	/**
	 * Rotates the vector by the passed amount. Positive rotation is counterclockwise, 
	 * and negative rotation is clockwise.
	 * 
	 * @param rotation - the angle to rotate
	 * @param isDegrees - whether or not the passed rotation is in degrees or radians
	 * @return The rotated vector.
	 */
	public Vec2d rotate(double rotation, boolean isDegrees)
	{
		if(isDegrees)
		{
			rotation *= Math.PI / 180d;
		}
		
		double l = this.getLength();
		double a = this.getAngle();
		
		return new Vec2d(a + rotation, false).scale(l);
	}
	
	/**
	 * Performs a dot product with the passed vector.
	 * 
	 * @param vec - the vector to dot with
	 * @return The dot product.
	 */
	public double dot(Vec2d vec)
	{
		return this.dot(vec.x, vec.y);
	}
	
	/**
	 * Performs a dot product with the passed vector.
	 * 
	 * @param posX - the vector's x coordinate
	 * @param posY - the vector's y coordinate
	 * @return The dot product.
	 */
	public double dot(double posX, double posY)
	{
		return this.x * posX + this.y * posY;
	}
	
	/**
	 * Performs a cross product with the passed vector.
	 * 
	 * @param vec - the vector to cross with
	 * @return The cross product.
	 */
	public double cross(Vec2d vec)
	{
		return this.cross(vec.x, vec.y);
	}
	
	/**
	 * Performs a cross product with the passed vector.
	 * 
	 * @param posX - the vector's x coordinate
	 * @param posY - the vector's y coordinate
	 * @return The cross product.
	 */
	public double cross(double posX, double posY)
	{
		return this.x * posY - this.y * posX;
	}
	
	@Override
	public String toString()
	{
		return "(" + this.x + ", " + this.y + ")";
	}
	
	@Override
	public boolean equals(Object obj)
	{
		return obj == this || (obj instanceof Vec2d && ((Vec2d) obj).x == this.x && ((Vec2d) obj).y == this.y);
	}
	
	@Override
	public Vec2d clone()
	{
		return new Vec2d(this.x, this.y);
	}
	
	@Override
	public int hashCode()
	{
		int hash = 7;
		hash = hash * 31 + Double.hashCode(this.x);
		hash = hash * 31 + Double.hashCode(this.y);
		return hash;
	}
}