package udpPlatform.client.particle;

public class Emitter {
	//TODO ps에서 생성할 것인가? 직접 생성할 것인가?

	//파티클을 생성하는 클래스
	ParticleSystem ps;
	private double x;
	private	double y;
	String region;
	//XXX
	static int EmitId;
	//TODO region 구현

	public Emitter(double x, double y, ParticleSystem ps) {
		this.ps = ps;
		this.x = x;
		this.y = y;
	}

	//한번에 n개 동시에 생성하는것
	public void burst(Particle particle, int num) {
		ps.emits.add(new Burst(particle, num));
	}

	//매 스탭마다 n개 까지 생성
	public void stream(Particle particle, int num) {
		ps.emits.add(new Stream(particle, num));
	}

	//발생 지역 설정
	public void setRegion(String region) {
		this.region = region;
	}

	public void setLocation(double x, double y) {
		this.x = x;
		this.y = y;
	}

	//발사작업 객체
	abstract class Emit implements Comparable<Emit>{
		//XXX
		protected int id;
		protected int num;
		Particle particle;
		protected abstract void emit();

		public Emit(Particle particle, int num) {
			this.particle = particle;
			this.num = num;
			//XXX
			this.id = EmitId++;
		}

		@Override
		public int compareTo(Emit o) {
			return this.id;
		}
	}

	class Stream extends Emit{
		public Stream(Particle particle, int num) {
			super(particle, num);
		}

		@Override
		protected void emit() {
			Particle particle = this.particle.generate();
			particle.x = Emitter.this.x;
			particle.y = Emitter.this.y;
			ps.particles.add(particle);
			num--;
		}
	}

	class Burst extends Emit{
		public Burst(Particle particle, int num) {
			super(particle, num);
		}

		@Override
		protected void emit() {
			for (int i = 0; i < num; i++) {
				Particle particle = this.particle.generate();
				particle.x = Emitter.this.x;
				particle.y = Emitter.this.y;
				ps.particles.add(particle);
			}
			this.num = 0;
		}
	}
}
