package udpPlatform.client.particle;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import javafx.scene.canvas.GraphicsContext;
import udpPlatform.client.particle.Emitter.Emit;

public class ParticleSystem {
	//TODO
	//attractor, destroyer, deflector 구현, rotation과 direction관계.
	//emitter shape 구현.
	//particle compare, hash 생각해보기.

	private GraphicsContext gc;
	protected Set<Particle> particles = new ConcurrentSkipListSet<Particle>();
	protected Set<Emit> emits = new HashSet<>();

	public int getPraticleNum(){
		return particles.size();
	}

	public ParticleSystem(GraphicsContext gc) {
		this.gc = gc;
	}

	public Emitter createEmitter() {
		Emitter emitter = new Emitter(0, 0, this);

		return emitter;
	}

	//입자 계산하는 메서드
	public void process() {
		Iterator<Emit> emitIterator = emits.iterator();
		while(emitIterator.hasNext()) {
			Emit emit = emitIterator.next();
			emit.emit();
			if (emit.num <= 0) {
				emitIterator.remove();
			}
		}

		Iterator<Particle> iterator = particles.iterator();
		while(iterator.hasNext()) {
			Particle particle = iterator.next();
			if (particle.getRemainLife() <= 0) {
				iterator.remove();
			}
			else {
				particle.step();
			}
		}
	}

	//그리는 메서드
	public void draw() {
		for (Particle particle : particles) {
			//파티클내부에서 gc의 여러변수를 건드리는데, 이걸 원복하기 위한 작업니다.
			gc.save();
			particle.draw(gc);
			gc.restore();
		}
	}

}
