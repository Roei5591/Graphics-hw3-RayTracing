package edu.cg.scene;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import edu.cg.Logger;
import edu.cg.UnimplementedMethodException;
import edu.cg.algebra.*;
import edu.cg.scene.camera.PinholeCamera;
import edu.cg.scene.lightSources.Light;
import edu.cg.scene.objects.Intersectable;
import edu.cg.scene.objects.Surface;

public class Scene {
	private String name = "scene";
	private int maxRecursionLevel = 1;
	private int antiAliasingFactor = 1; //gets the values of 1, 2 and 3
	private boolean renderRefarctions = false;
	private boolean renderReflections = false;
	
	private PinholeCamera camera;
	private Vec ambient = new Vec(1, 1, 1); //white
	private Vec backgroundColor = new Vec(0, 0.5, 1); //blue sky
	private List<Light> lightSources = new LinkedList<>();
	private List<Surface> surfaces = new LinkedList<>();
	
	
	//MARK: initializers
	public Scene initCamera(Point eyePoistion, Vec towardsVec, Vec upVec,  double distanceToPlain) {
		this.camera = new PinholeCamera(eyePoistion, towardsVec, upVec,  distanceToPlain);
		return this;
	}
	
	public Scene initAmbient(Vec ambient) {
		this.ambient = ambient;
		return this;
	}
	
	public Scene initBackgroundColor(Vec backgroundColor) {
		this.backgroundColor = backgroundColor;
		return this;
	}
	
	public Scene addLightSource(Light lightSource) {
		lightSources.add(lightSource);
		return this;
	}
	
	public Scene addSurface(Surface surface) {
		surfaces.add(surface);
		return this;
	}
	
	public Scene initMaxRecursionLevel(int maxRecursionLevel) {
		this.maxRecursionLevel = maxRecursionLevel;
		return this;
	}
	
	public Scene initAntiAliasingFactor(int antiAliasingFactor) {
		this.antiAliasingFactor = antiAliasingFactor;
		return this;
	}
	
	public Scene initName(String name) {
		this.name = name;
		return this;
	}
	
	public Scene initRenderRefarctions(boolean renderRefarctions) {
		this.renderRefarctions = renderRefarctions;
		return this;
	}
	
	public Scene initRenderReflections(boolean renderReflections) {
		this.renderReflections = renderReflections;
		return this;
	}
	
	//MARK: getters
	public String getName() {
		return name;
	}
	
	public int getFactor() {
		return antiAliasingFactor;
	}
	
	public int getMaxRecursionLevel() {
		return maxRecursionLevel;
	}
	
	public boolean getRenderRefarctions() {
		return renderRefarctions;
	}
	
	public boolean getRenderReflections() {
		return renderReflections;
	}
	
	@Override
	public String toString() {
		String endl = System.lineSeparator(); 
		return "Camera: " + camera + endl +
				"Ambient: " + ambient + endl +
				"Background Color: " + backgroundColor + endl +
				"Max recursion level: " + maxRecursionLevel + endl +
				"Anti aliasing factor: " + antiAliasingFactor + endl +
				"Light sources:" + endl + lightSources + endl +
				"Surfaces:" + endl + surfaces;
	}
	
	private transient ExecutorService executor = null;
	private transient Logger logger = null;
	
	private void initSomeFields(int imgWidth, int imgHeight, Logger logger) {
		this.logger = logger;
	}
	
	
	public BufferedImage render(int imgWidth, int imgHeight, double viewPlainWidth,Logger logger)
			throws InterruptedException, ExecutionException {

		initSomeFields(imgWidth, imgHeight, logger);
		
		BufferedImage img = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB);
		camera.initResolution(imgHeight, imgWidth, viewPlainWidth);
		int nThreads = Runtime.getRuntime().availableProcessors();
		nThreads = nThreads < 2 ? 2 : nThreads;
		this.logger.log("Intitialize executor. Using " + nThreads + " threads to render " + name);
		executor = Executors.newFixedThreadPool(nThreads);
		
		@SuppressWarnings("unchecked")
		Future<Color>[][] futures = (Future<Color>[][])(new Future[imgHeight][imgWidth]);
		
		this.logger.log("Starting to shoot " +
			(imgHeight*imgWidth*antiAliasingFactor*antiAliasingFactor) +
			" rays over " + name);
		
		for(int y = 0; y < imgHeight; ++y)
			for(int x = 0; x < imgWidth; ++x)
				futures[y][x] = calcColor(x, y);
		
		this.logger.log("Done shooting rays.");
		this.logger.log("Wating for results...");
		
		for(int y = 0; y < imgHeight; ++y)
			for(int x = 0; x < imgWidth; ++x) {
				Color color = futures[y][x].get();
				img.setRGB(x, y, color.getRGB());
			}
		
		executor.shutdown();
		
		this.logger.log("Ray tracing of " + name + " has been completed.");
		
		executor = null;
		this.logger = null;
		
		return img;
	}
	
	private Future<Color> calcColor(int x, int y) {
		return executor.submit(() -> {
			Vec color = new Vec();
			if(antiAliasingFactor == 1){
				Point centerPoint = camera.transform(x, y);
				Ray ray = new Ray(camera.getCameraPosition(), centerPoint);
				color = calcColor(ray, maxRecursionLevel );
			}else{
				List<Vec> colors = new LinkedList<>();
				List<Point> Points = camera.transformAntiAliasing(x, y , antiAliasingFactor);
				for (Point point :Points ) {
					Ray ray = new Ray(camera.getCameraPosition(), point);
					colors.add(calcColor(ray, maxRecursionLevel ));
				}
				for (Vec vector : colors) {
					color = color.add(vector);
				}
				color.x =  color.x / Math.pow(antiAliasingFactor, 2);
				color.y =  color.y / Math.pow(antiAliasingFactor, 2);
				color.z =  color.z / Math.pow(antiAliasingFactor, 2);
			}
			return color.toColor();
		});
	}
	
	private Vec calcColor(Ray ray, int recusionLevel) {

        if(recusionLevel <= 0) return new Vec();

		Hit minHit = getMinHit(ray);

		if(minHit == null) return backgroundColor;
        Point pointOfClosestHit = ray.add(minHit.t());

		//calculate it's color
        Vec I = new Vec();
        Vec a = minHit.getSurface().Ka();
        Vec b = a.mult(ambient);
        I = I.add(b); //Ka* Iamb

        for (Light lightSource : lightSources) {
            Ray shadowRay = lightSource.rayToLight(pointOfClosestHit);
            if(surfaces.stream().allMatch(x -> !lightSource.isOccludedBy(x, shadowRay))){
                Vec calculateLightDependent = CalculateLightDependent(ray, minHit, shadowRay);
                Vec Il = lightSource.intensity(pointOfClosestHit,shadowRay);
                Vec LightSpecificVector = calculateLightDependent.mult(Il);
                I = I.add(LightSpecificVector);
            }
        }


		Vec Ir = GetReflectionIntensity(ray, recusionLevel, minHit);
		double kr = minHit.getSurface().reflectionIntensity();
		Vec c = Ir.mult(kr);
        I = I.add(c);

        if(minHit.getSurface().isTransparent()){
			Vec It = GetRefractionIntensity(ray, recusionLevel, minHit);
			double kt = minHit.getSurface().refractionIntensity();
			I = I.add(It.mult(kt));
		}

        return I;
	}

	private Vec GetRefractionIntensity(Ray ray, int recusionLevel, Hit minHit) {
		Vec RefractionDirection = Ops.refract(ray.direction(), minHit.getNormalToSurface(), minHit.getSurface().n1(minHit), minHit.getSurface().n2(minHit) );
		Ray RefractionRay = new Ray(ray.add(minHit.t()),RefractionDirection);
		return calcColor(RefractionRay ,recusionLevel - 1);
	}

	private Vec GetReflectionIntensity(Ray ray, int recusionLevel, Hit minHit) {
		Vec ReflectionDirection = Ops.reflect(ray.direction(), minHit.getNormalToSurface());
		Ray ReflectionRay = new Ray(ray.add(minHit.t()),ReflectionDirection);
		return calcColor(ReflectionRay,recusionLevel - 1);
	}

	public Hit getMinHit(Ray ray) {
		Hit minHit = null;
		double minT = Double.MAX_EXPONENT;
		for (Surface surface : surfaces) {
		    Hit hitOfSurface = surface.intersect(ray) ;
		    if(hitOfSurface != null && hitOfSurface.t() < minT){
                minT = hitOfSurface.t();
                minHit = hitOfSurface;
                minHit.setSurface(surface);
            }
		}
		return minHit;
	}

	private Vec CalculateLightDependent(Ray ray, Hit minHit, Ray shadowRay) {
	    Vec Kd = minHit.getSurface().Kd();
	    Vec N = minHit.getNormalToSurface();
	    Vec L = shadowRay.direction();
        Vec diffue = Kd.mult(N.dot(L));

        Vec Ks = minHit.getSurface().Ks();
        Vec V = ray.direction().neg();
        //Vec R = Ops.reflect(shadowRay.direction().neg(), minHit.getNormalToSurface());
        Vec R = Ops.reflect(shadowRay.direction(), minHit.getNormalToSurface());
        Vec speclar = Ks.mult(Math.pow(V.dot(R),  minHit.getSurface().shininess()));

        return diffue.add(speclar);
    }
}
