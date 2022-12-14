package udpPlatform.client.particle;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;

public abstract class Particle implements Cloneable, Comparable<Particle>{

	//XXX
	static int lastId = 0;
	int id = 0;

	protected double x;
	protected double y;

	protected double alpha = 1;
	protected double alpha1 = 1;
	protected double alpha2 = 1;
	protected double alpha3 = 1;

	protected BlendMode blendMode = BlendMode.ADD;

	protected double direction = 0;
	protected double direction_min = 0;
	protected double direction_max = 0;
	protected double direction_wiggle = 0;
	protected double direction_increase = 0;

	private double xSpeed_by_gravity = 0;
	private double ySpeed_by_gravity = 0;
	protected double gravity = 0;
	protected double gravity_direction = 270;

	protected int lifespan = 60;
	protected int lifespan_min = 60;
	protected int lifespan_max = 60;

	protected double size = 2;
	protected double size_min = 2;
	protected double size_max = 2;
	protected double size_increase = 0;
	protected double size_wiggle = 0;

	protected double speed;
	protected double speed_min = 1;
	protected double speed_max = 1;
	protected double speed_increase = 0;
	protected double speed_wiggle = 0;

	protected double rotation = 0;
	protected double rotation_min = 0;
	protected double rotation_max = 0;
	protected double rotation_wiggle = 0;

	protected double xScale = 1;
	protected double yScale = 1;

	protected Color color = Color.RED;
	protected Color color1 = Color.RED;
	protected Color color2 = Color.RED;
	protected Color color3 = Color.RED;

	//이미지가 우선. 없으면 shape 그림.
	//protected Image sprite = null;

	protected String shape = "circle";
	//circle, square ,line
	//circleLine, squareLine
	private int round = 0;
	private int wiggle = 0;
	private boolean isUp = true;

	//변수에 대입
	public void step() {
		//변수갱신
		if (round > lifespan/3*2) {
			color = color3;
			alpha = alpha3;
		}
		else if(round > lifespan/3) {
			color = color2;
			alpha = alpha2;
		}
		else {
			color = color1;
			alpha = alpha1;
		}

		switch (wiggle) {
			case 1:
				isUp = false;
				wiggle--;
				break;
			case 0:
				if (isUp)
					wiggle++;
				else
					wiggle--;
				break;
			case -1:
				isUp = true;
				wiggle++;
				break;
			default:
				//예외처리
				wiggle = 0;
				break;
		}

		//변수 계산
		direction += direction_increase + direction_wiggle * wiggle;
		xSpeed_by_gravity += gravity * Math.cos(gravity_direction/180*Math.PI);
		ySpeed_by_gravity += gravity * Math.sin(gravity_direction/180*Math.PI);
		size += size_increase + size_wiggle * wiggle;
		speed += speed_increase + speed_wiggle;
		rotation += rotation_wiggle;
		//XXX
		rotation = direction;


		x += speed * Math.cos(direction/180*Math.PI) + xSpeed_by_gravity;
		y += speed * Math.sin(direction/180*Math.PI) + ySpeed_by_gravity;
		round++;
	}

	//자신을 생성한다. : 실패시 null 반환
	//XXX 내가봤을때, return 하지말고 그냥 추가하도록 해야한다.
	public Particle generate() {
		Particle particle = null;
		try {
			particle = (Particle) this.clone();
			particle.direction = direction_min + Math.random() * (direction_max-direction_min);
			particle.speed = speed_min + Math.random() * (speed_max - speed_min);
			particle.lifespan = lifespan_min + (int)(Math.random() * (lifespan_max - lifespan_min));
			particle.rotation = rotation_min + Math.random() * (rotation_max - rotation_min);
			particle.size = size_min + Math.random() * (size_max-size_min);
			particle.id = lastId++;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return particle;
	}

	//남은생명getter
	public int getRemainLife() {
		return this.lifespan - this.round;
	}

	//자신을 그린다.
	public void draw(GraphicsContext gc) {
		//중심설정
		double x = this.x-size/2;
		double y = this.y-size/2;

		gc.setGlobalBlendMode(blendMode);
		gc.setGlobalAlpha(alpha);
		gc.setFill(color);

		//transform 설정
		Affine xform = gc.getTransform();
		xform.appendRotation(rotation, x, y);
		xform.appendScale(xScale, yScale, x, y);
		gc.setTransform(xform);

		switch (shape) {
			case "circle":
				gc.fillOval(x, y, size, size);
				break;
			case "square":
				gc.fillRect(x, y, size, size);
				break;
			case "line":
				gc.fillRect(x, y, size, size/6);
				break;
			case "circleLine":
				gc.strokeOval(x, y, size, size);
				break;
			case "squareLine":
				gc.strokeRect(x, y, size, size);
				break;

			default:
				break;
		}
	}

	//XXX
	@Override
	public int compareTo(Particle o) {
		return o.id - id;
	}
}
