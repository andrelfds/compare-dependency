package controller;

import java.io.File;
import java.util.logging.Level;

import org.apache.ivy.Ivy;
import org.apache.ivy.core.module.descriptor.DefaultDependencyDescriptor;
import org.apache.ivy.core.module.descriptor.DefaultModuleDescriptor;
import org.apache.ivy.core.module.descriptor.DependencyDescriptor;
import org.apache.ivy.core.module.descriptor.ModuleDescriptor;
import org.apache.ivy.core.module.id.ModuleRevisionId;
import org.apache.ivy.core.report.ResolveReport;
import org.apache.ivy.core.resolve.ResolveOptions;
import org.apache.ivy.core.retrieve.RetrieveOptions;
import org.apache.ivy.core.settings.IvySettings;
import org.apache.ivy.util.Message;

public class ivy {
	
	 public static void main(String[] args) throws Exception {

	        String groupId = "org.springframework";
	        String artifactId = "spring-context-support";
	        String version = "4.0.2.RELEASE";
	        File   out = new File("out");
	        
	        IvySettings ivySettings = new IvySettings();
	        File f = new File("ivy/ivy.xml");
            ivySettings.loadProperties(f);
	        //ivySettings.setDefaultCache(new File("ivy/cache"));

	        // use the biblio resolver, if you consider resolving 
	        // POM declared dependencies
//	        IBiblioResolver br = new IBiblioResolver();
//	        br.setM2compatible(true);
//	        br.setUsepoms(true);
//	        br.setName("central");
//
//	        ivySettings.addResolver(br);
//	        ivySettings.setDefaultResolver(br.getName());
	        ivySettings.getResolvers();
	        Ivy ivy = Ivy.newInstance(ivySettings);

	        // Step 1: you always need to resolve before you can retrieve
	        //
	        ResolveOptions ro = new ResolveOptions();
	        // this seems to have no impact, if you resolve by module descriptor (in contrast to resolve by ModuleRevisionId)
	        ro.setTransitive(true);
	        // if set to false, nothing will be downloaded
	        ro.setDownload(true);

	        // 1st create an ivy module (this always(!) has a "default" configuration already)
	        DefaultModuleDescriptor md = DefaultModuleDescriptor.newDefaultInstance(
	            // give it some related name (so it can be cached)
	            ModuleRevisionId.newInstance(
	                groupId, 
	                artifactId+"-envelope", 
	                version
	            )
	        );

	        // 2. add dependencies for what we are really looking for
	        ModuleRevisionId ri = ModuleRevisionId.newInstance(
	            groupId, 
	            artifactId,
	            version
	        );
	        // don't go transitive here, if you want the single artifact
	        DefaultDependencyDescriptor dd = new DefaultDependencyDescriptor(md, ri, false, false, false);

	        // map to master to just get the code jar. See generated ivy module xmls from maven repo
	        // on how configurations are mapped into ivy. Or check 
	        // e.g. http://lightguard-jp.blogspot.de/2009/04/ivy-configurations-when-pulling-from.html
	        dd.addDependencyConfiguration("default", "master");
	        md.addDependency(dd);
	        

	        // now resolve
	        ResolveReport rr = ivy.resolve(md,ro);
	        if (rr.hasError()) {
	            throw new RuntimeException(rr.getAllProblemMessages().toString());
	        }

	        // Step 2: retrieve
	        ModuleDescriptor m = rr.getModuleDescriptor();
	        
	        DependencyDescriptor[] dependencies = m.getDependencies();
	        
	        for (int i = 0; i < dependencies.length; i++) {
	        	System.out.println(dependencies[i].getDependencyRevisionId().getName());
	        	System.out.println(dependencies[i].getDependencyRevisionId().getRevision());
		    	 
		     }

	        ivy.retrieve(
	            m.getModuleRevisionId(),
	            out.getAbsolutePath()+"/[artifact](-[classifier]).[ext]",
	            new RetrieveOptions()
	                // this is from the envelop module
	                .setConfs(new String[]{"default"})
	        );
	    }
	 
	 
	 public Ivy getIvy(File localFilePath, String propertyFile) {
	            try {
	                IvySettings ivySettings = new IvySettings();
	                if (propertyFile != null) {
		                File f = new File(localFilePath,propertyFile);
		                ivySettings.loadProperties(f);
		               
	                }
	           
	                return Ivy.newInstance(ivySettings);
	            } 
	            catch (Exception e) {
	               e.getMessage();
	            }
	            return null;
	    }


}
