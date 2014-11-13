package riv.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import riv.objects.OrderByable;
import riv.objects.config.AppConfig1;
import riv.objects.config.AppConfig2;
import riv.objects.config.EnviroCategory;
import riv.objects.config.ProjectCategory;
import riv.objects.config.Status;
import riv.objects.profile.Profile;
import riv.objects.profile.ProfileItemGeneralWithout;
import riv.objects.profile.ProfileItemGoodWithout;
import riv.objects.profile.ProfileItemLabourWithout;
import riv.objects.profile.ProfileProduct;
import riv.objects.profile.ProfileProductBase;
import riv.objects.profile.ProfileProductWithout;
import riv.objects.project.Block;
import riv.objects.project.BlockBase;
import riv.objects.project.BlockWithout;
import riv.objects.project.Project;
import riv.objects.project.ProjectItem;
import riv.objects.project.ProjectItemAssetWithout;
import riv.objects.project.ProjectItemContribution;
import riv.objects.project.ProjectItemGeneralWithout;
import riv.objects.project.ProjectItemLabourWithout;
import riv.objects.project.ProjectItemPersonnelWithout;
import riv.objects.project.ProjectItemServiceWithout;
import riv.objects.reference.ReferenceCost;
import riv.objects.reference.ReferenceIncome;
import riv.objects.reference.ReferenceLabour;
import riv.web.config.RivConfig;

@Component
public class Upgrader {
	static final Logger LOG = LoggerFactory.getLogger(Upgrader.class);
	
	@Autowired
	private RivConfig rivConfig;
	
	public void upgradeRivConfig(RivConfig rcNew) {
		// <3.1 add decimal separator, thousand separator and decimal length
		if (rcNew.getSetting().getDecimalLength()==null) {
			rcNew.getSetting().setDecimalLength(2);
			rcNew.getSetting().setDecimalSeparator(".");
			rcNew.getSetting().setThousandSeparator(",");
		}
		
		// <2.0 project category -1 changed to -5
		if (rcNew.getCategories().containsKey(-1)) {
			ProjectCategory pc = rcNew.getCategories().get(-1);
			rcNew.getCategories().remove(-1);
			pc.setConfigId(-5);
			rcNew.getCategories().put(-5, pc);
		}
		
		// check project statuses
		// RIV < 2.2
		// incoming rivConfig has no statses and RIV2.2 default statuses have not yet been added
		if (rcNew.getStatuses()==null &! rivConfig.getStatuses().containsKey(-20)) { 
			rcNew.setStatuses(new HashMap<Integer, Status>());
			Status s = new Status();
			s.setDescription("Proposal");
			s.setConfigId(-20);
			rivConfig.storeAppConfig(s);
			s = new Status();
			s.setDescription("Approved");
			s.setConfigId(-21);
			rivConfig.storeAppConfig(s);
			s = new Status();
			s.setDescription("Investment");
			s.setConfigId(-22);
			rivConfig.storeAppConfig(s);
			s = new Status();
			s.setDescription("Operational");
			s.setConfigId(-23);
			rivConfig.storeAppConfig(s);
		}
		
		// check enviro categories
		// RIV < 2.0
		// incoming rivConfig has no enviroCategories and RIV2.0 enviro categories have not yet been added
		if (rcNew.getEnviroCategories() == null &! rivConfig.getEnviroCategories().containsKey(-30)) {
			rcNew.setEnviroCategories(new HashMap<Integer, EnviroCategory>());
			EnviroCategory a = new EnviroCategory();
			a.setDescription("A");
			a.setConfigId(-30);
			rivConfig.storeAppConfig(a);
			EnviroCategory b = new EnviroCategory();
			b.setDescription("B");
			b.setConfigId(-31);
			rivConfig.storeAppConfig(b);
			EnviroCategory c = new EnviroCategory();
			c.setDescription("C");
			c.setConfigId(-32);
			rivConfig.storeAppConfig(c);
			EnviroCategory d = new EnviroCategory();
			d.setDescription("D");
			d.setConfigId(-33);
		}
		
		if (rcNew.getAppConfig1s()==null) {
			rcNew.setAppConfig1s(new HashMap<Integer, AppConfig1>());
		}
		if (rcNew.getAppConfig2s()==null) {
			rcNew.setAppConfig2s(new HashMap<Integer, AppConfig2>());
		}
		if (rcNew.getStatuses()==null) {
			rcNew.setStatuses(new HashMap<Integer, Status>());
		}
		if (rcNew.getEnviroCategories()==null) {
			rcNew.setEnviroCategories(new HashMap<Integer, EnviroCategory>());
		}
	}
	
	public void upgradeProfile(Profile profile) {
		// if <2.0 add refitems and length unit
		if (profile.getRefCosts()==null) {
			profile.setRefCosts(new HashSet<ReferenceCost>());
			profile.setRefIncomes(new HashSet<ReferenceIncome>());
			profile.setRefLabours(new HashSet<ReferenceLabour>());
			for (ProfileProduct prod : profile.getProducts()) {
				prod.setLengthUnit(0); // default is months
			}
		}
		
		// <4.0 separate ProfileProductWithout from ProfileProducts and re-order both collections
		addOrders(profile.getProducts());
		if (profile.getProductsWithout()==null) {
			profile.setProductsWithout(new HashSet<ProfileProductWithout>());
			int orderWithout=0;
			int orderWith=0;
			
			@SuppressWarnings({ "rawtypes", "unchecked" })
			TreeSet myBlocks = new TreeSet(profile.getProducts());
			for (Object o : myBlocks) {
				ProfileProductBase b = (ProfileProductBase)o;
				if (b.getClass()==ProfileProductWithout.class) {
					profile.getProducts().remove(b);
					b.setOrderBy(orderWithout++);
					profile.getProductsWithout().add((ProfileProductWithout)b);
				} else {
					b.setOrderBy(orderWith++);
				}
			}
		}
		
		if (profile.isMissingOrders()) { profile.addOrders(); }
		if (profile.isBlockOrderProblem()) { profile.correctBlockOrderProblem(); }
		
		
		// RIV <2.2: set status to generic
		if (profile.getStatus()==null) {
			profile.setStatus(rivConfig.getStatuses().get(-7));
		}
		
		//if <4.0 add investment and general costs without project
		if (profile.getGlsGeneralWithout()==null) {
			profile.setGlsGoodsWithout(new HashSet<ProfileItemGoodWithout>());
//		}
//		if (profile.getGlsGoodsWithout()==null) {
			profile.setGlsLaboursWithout(new HashSet<ProfileItemLabourWithout>());
			profile.setGlsGeneralWithout(new HashSet<ProfileItemGeneralWithout>());
		}
		
		profile.importRefLinks();
		
	}
	
	public void upgradeProject(Project project) {
		// <4.0 separate BlocksWithout from Blocks and re-order both collections
		addOrders(project.getBlocks());
		if (project.getBlocksWithout()==null) {
			project.setBlocksWithout(new HashSet<BlockWithout>());
			int orderWithout=0;
			int orderWith=0;
			
			@SuppressWarnings({ "rawtypes", "unchecked" })
			TreeSet myBlocks = new TreeSet(project.getBlocks());
			for (Object o : myBlocks) {
				BlockBase b = (BlockBase)o;
				if (b.getClass()==BlockWithout.class) {
					project.getBlocks().remove(b);
					b.setOrderBy(orderWithout++);
					project.getBlocksWithout().add((BlockWithout)b);
				} else {
					b.setOrderBy(orderWith++);
				}
			}
		}
				
		// if <2.0 add refitems and length unit
		if (project.getRefCosts()==null) {
			project.setRefCosts(new HashSet<ReferenceCost>());
			project.setRefIncomes(new HashSet<ReferenceIncome>());
			project.setRefLabours(new HashSet<ReferenceLabour>());
			for (Block block : project.getBlocks()) {
				block.setLengthUnit(0); // default is months
			}
			for (BlockWithout block : project.getBlocksWithout()) {
				block.setLengthUnit(0); // default is months
			}
		}
		
		// RIV <2.0 set enviro category to generic 
		if (project.getEnviroCategory() == null) {
			project.setEnviroCategory(rivConfig.getEnviroCategories().get(-6));
		}
		
		// if <2.2 set block cycleFirstYearIncome
		for (Block b : project.getBlocks()) {
			if (b.getCycleFirstYearIncome()!=null) {
				break;
			} else {
				b.setCycleFirstYearIncome(b.getCycleFirstYear());
			}
		}
		for (BlockWithout b : project.getBlocksWithout()) {
			if (b.getCycleFirstYearIncome()!=null) {
				break;
			} else {
				b.setCycleFirstYearIncome(b.getCycleFirstYear());
			}
		}
		
		// RIV <2.2: set status to generic
		if (project.getStatus()==null) {
			project.setStatus(rivConfig.getStatuses().get(-7));
		}

		// error in RIV 2.1-2.4: Generic project still contains local enviro category
		if (project.isGeneric()) {
			project.setEnviroCategory(rivConfig.getEnviroCategories().get(-6));
		}
		
		// RIV <3.0 set appConfig1&2 to generic
		if (project.getAppConfig1()==null) { project.setAppConfig1(rivConfig.getAppConfig1s().get(-8)); }
		if (project.getAppConfig2()==null) { project.setAppConfig2(rivConfig.getAppConfig2s().get(-9)); }
		
		
		// if <3.0 add general costs without project
		if (project.getGeneralWithouts()==null) {
			project.setGeneralWithouts(new HashSet<ProjectItemGeneralWithout>());
			project.setPersonnelWithouts(new HashSet<ProjectItemPersonnelWithout>());
		}
		//if <4.0 add investment costs without project
		if (project.getAssetsWithout()==null) {
			project.setAssetsWithout(new HashSet<ProjectItemAssetWithout>());
			project.setLaboursWithout(new HashSet<ProjectItemLabourWithout>());
			project.setServicesWithout(new HashSet<ProjectItemServiceWithout>());
		}	
		
		//<RIV2.(?) if table items are missing orderBy, add correct values
		correctMissingOrders(project);	
				
		// import reference links using orderby field
		project.importRefLinks();
		
		// <RIV4.1 NIG project contributions need year
		if (! project.getIncomeGen()) {
			for (ProjectItemContribution c : project.getContributions()) {
				if (c.getYear()!=null) {break;}
				c.setYear(1);
				c.setContributor("");
			}
		}

	}
	
	/**
	 * To ensure smooth importing from previous versions we will modify the XML
	 * @param source XML source to be converted
	 * @return Updated XML source
	 * @throws IOException 
	 */
	public byte[] upgradeXml(byte[] source) throws IOException {
		
				    	  
			TransformerFactory factory = TransformerFactory.newInstance();
			Source xslt = new StreamSource(Thread.currentThread().getContextClassLoader().getResourceAsStream("upgrade.xslt"));
			Source in=new StreamSource(new ByteArrayInputStream(source));
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			Transformer transformer=null;
			try {
				transformer = factory.newTransformer(xslt);
				transformer.transform(in, new StreamResult(baos));
			} catch (TransformerConfigurationException e) {
				LOG.error("Error creating xsl transformer.",e);
			} catch (TransformerException e) {
				LOG.error("Error transforming xsl.",e);
			}
			// for testing:
			// baos.writeTo(new FileOutputStream(new File("")));
			return baos.toByteArray();
		
	}
	
	/**
	 * checks if project table rows are missing orders (imported from RIV < 2.0)
	 * checks asset table since rows are required, so only one table needs to be checked
	 * @return true if orders are missing
	 */
	public static void correctMissingOrders(Project p) {
		boolean missingOrders=false;
		for (ProjectItem item : p.getAssets()) { 
			if (missingOrders==false && item.getOrderBy()==null) { 
				missingOrders=true;
			}
		}
		if (missingOrders) {
			addOrders(p.getAssets());
			addOrders(p.getLabours());
			addOrders(p.getServices());
			addOrders(p.getGenerals());
			addOrders(p.getPersonnels());
			addOrders(p.getContributions());
			addOrders(p.getNongenMaterials());
			addOrders(p.getNongenLabours());
			addOrders(p.getNongenMaintenance());
			// for each project block
			for (Block block : p.getBlocks()) {
				addOrders(block.getIncomes());
				addOrders(block.getInputs());
				addOrders(block.getLabours());
			}
			for (BlockWithout block : p.getBlocksWithout()) {
				addOrders(block.getIncomes());
				addOrders(block.getInputs());
				addOrders(block.getLabours());
			}
		}
	}
	
	public static void addOrders(Set<? extends OrderByable> set) {
		try {
			TreeSet<OrderByable> myItems = new TreeSet<OrderByable>(set);
			int i=0;
			for (OrderByable item : myItems) {
				item.setOrderBy(i++);
			}
		} catch (NullPointerException e) {
			// orderBy is null, can't compare to make TreeSet
			int i=0;
			for (OrderByable o : set) {
				o.setOrderBy(i++);
			}
		}
	}
}
