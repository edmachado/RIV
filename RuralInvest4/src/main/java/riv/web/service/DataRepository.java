package riv.web.service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import riv.objects.AttachedFile;
import riv.objects.FilterCriteria;
import riv.objects.HasDonations;
import riv.objects.HomeData;
import riv.objects.Probase;
import riv.objects.config.AppConfig;
import riv.objects.config.AppConfig1;
import riv.objects.config.AppConfig2;
import riv.objects.config.Beneficiary;
import riv.objects.config.EnviroCategory;
import riv.objects.config.FieldOffice;
import riv.objects.config.ProjectCategory;
import riv.objects.config.Setting;
import riv.objects.config.Status;
import riv.objects.config.User;
import riv.objects.config.Version;
import riv.objects.profile.Profile;
import riv.objects.profile.ProfileFile;
import riv.objects.profile.ProfileItem;
import riv.objects.profile.ProfileItemGeneral;
import riv.objects.profile.ProfileItemGeneralWithout;
import riv.objects.profile.ProfileItemGood;
import riv.objects.profile.ProfileItemGoodWithout;
import riv.objects.profile.ProfileItemLabour;
import riv.objects.profile.ProfileItemLabourWithout;
import riv.objects.profile.ProfileProduct;
import riv.objects.profile.ProfileProductBase;
import riv.objects.profile.ProfileProductIncome;
import riv.objects.profile.ProfileProductInput;
import riv.objects.profile.ProfileProductItem;
import riv.objects.profile.ProfileProductLabour;
import riv.objects.profile.ProfileProductWithout;
import riv.objects.profile.ProfileResult;
import riv.objects.project.Block;
import riv.objects.project.BlockBase;
import riv.objects.project.BlockIncome;
import riv.objects.project.BlockInput;
import riv.objects.project.BlockItem;
import riv.objects.project.BlockLabour;
import riv.objects.project.BlockWithout;
import riv.objects.project.Donor;
import riv.objects.project.Project;
import riv.objects.project.ProjectFile;
import riv.objects.project.ProjectItem;
import riv.objects.project.ProjectItemAsset;
import riv.objects.project.ProjectItemAssetWithout;
import riv.objects.project.ProjectItemContribution;
import riv.objects.project.ProjectItemContributionPerYear;
import riv.objects.project.ProjectItemGeneral;
import riv.objects.project.ProjectItemGeneralBase;
import riv.objects.project.ProjectItemGeneralPerYear;
import riv.objects.project.ProjectItemGeneralWithout;
import riv.objects.project.ProjectItemLabour;
import riv.objects.project.ProjectItemLabourWithout;
import riv.objects.project.ProjectItemNongenLabour;
import riv.objects.project.ProjectItemNongenMaintenance;
import riv.objects.project.ProjectItemNongenMaterials;
import riv.objects.project.ProjectItemPersonnel;
import riv.objects.project.ProjectItemPersonnelWithout;
import riv.objects.project.ProjectItemService;
import riv.objects.project.ProjectItemServiceWithout;
import riv.objects.project.ProjectResult;
import riv.objects.reference.ReferenceCost;
import riv.objects.reference.ReferenceIncome;
import riv.objects.reference.ReferenceItem;
import riv.objects.reference.ReferenceLabour;

@Transactional
@Repository
public class DataRepository {
	static final Logger LOG = LoggerFactory.getLogger(DataRepository.class);
	
	@Autowired
	private SessionFactory sessionFactory;

	private final Session currentSession() {
		return sessionFactory.getCurrentSession();
	}
	
	public void updateLogin(String username) {
		SQLQuery q = currentSession().createSQLQuery("UPDATE user SET last_login=NOW() WHERE username=:username");
		q.setString("username", username);
		q.executeUpdate();
	}
	
	public void setPageSize(User u, int size) {
		Query q = currentSession().createQuery("update User u set pageSize=:s where u.userId=:u");
		q.setParameter("s", size);
		q.setParameter("u", u.getUserId());
		q.executeUpdate();
	}
	
	public void makeAdmin(User u, boolean admin) {
		Query q = currentSession().createQuery("update User u set administrator=:a where u.userId=:u");
		q.setParameter("a", admin);
		q.setParameter("u", u.getUserId());
		q.executeUpdate();
	}
	
	@SuppressWarnings("unchecked")
	public void deleteAll(boolean project, boolean incomeGen) {
		Criteria c = currentSession().createCriteria(project ? Project.class : Profile.class).add(Restrictions.eq("incomeGen", incomeGen));	

		if (project) {
			List<Project> results = c.list();
			for (Project p : results) {
				deleteProject(p);
			}
		} else {
			List<Profile> results = c.list();
			for (Profile p : results) {
				deleteProfile(p);
			}
		}
	}
	
	public void deleteAllAppConfigs() {
		Query q = currentSession().createQuery("delete from AppConfig where configId not in  (-4,-3,-5,-2,-6,-7,-8,-9)");
		q.executeUpdate();
	}

	public void replaceProjectContribution(int projectId, List<ProjectItemContribution> items) {
		Project p = getProject(projectId, 10);
		p.getContributions().clear();
		for (ProjectItemContribution i : items) {
			p.addContribution(i);
		}
	}
	
	public void replaceProfileProduct(int productId, List<ProfileProductIncome> incs, List<ProfileProductInput> inps, List<ProfileProductLabour> labs) {
		ProfileProductBase pp = getProfileProduct(productId, "all");
		pp.getProfileIncomes().clear();
		for (ProfileProductIncome item : incs) {
			pp.addProfileIncome(item);
		}
		pp.getProfileInputs().clear();
		for (ProfileProductInput item : inps) {
			pp.addProfileInput(item);
		}
		pp.getProfileLabours().clear();
		for (ProfileProductLabour item : labs) {
			pp.addProfileLabour(item);
		}
	}
	
	public void replaceProfileGeneral(int profileId, List<ProfileItemGeneral> gens, List<ProfileItemGeneralWithout> gensWo) {
		Profile p = getProfile(profileId, 5);
		p.getGlsGeneral().clear();
		for (ProfileItemGeneral item : gens) {
			p.addGlsGeneral(item);
		}
		
		if (gensWo != null) {
			p.getGlsGeneralWithout().clear();
			for (ProfileItemGeneralWithout item : gensWo) {
				p.addGlsGeneralWithout(item);
			}
		}
	}
	
	public void replaceProjectGeneralNongen(int projectId, List<ProjectItemNongenMaterials> materials, List<ProjectItemNongenLabour> labours, List<ProjectItemNongenMaintenance> maints) {
		Project p = getProject(projectId, 8);
		p.getNongenMaterials().clear();
		for (ProjectItemNongenMaterials i : materials) {
			p.addNongenMaterial(i);
		}
		p.getNongenLabours().clear();
		for (ProjectItemNongenLabour i : labours) {
			p.addNongenLabour(i);
		}
		p.getNongenMaintenance().clear();
		for (ProjectItemNongenMaintenance i : maints) {
			p.addNongenMaintenance(i);
		}
		
		storeProject(p, p.getWizardStep()==null);
	}
	
	public void replaceBlock(int blockId, List<BlockIncome> incs, List<BlockInput> inps, List<BlockLabour> labs) {		
		BlockBase b = getBlock(blockId, "all");
		b.getIncomes().clear();
		for (BlockIncome i : incs) {
			b.addIncome(i);
		}
		b.getInputs().clear();
		for(BlockInput i : inps) {
			b.addInput(i);
		}
		b.getLabours().clear();
		for(BlockLabour i : labs) {
			b.addLabour(i);
		}
		
		storeBlock(b);
		
	}
	
	public void replaceProjectGeneral(int projectId, List<ProjectItemGeneral> gens, List<ProjectItemPersonnel> pers, List<ProjectItemGeneralWithout> gensWo, List<ProjectItemPersonnelWithout> persWo) {
		Project p = getProject(projectId, 8);
		p.getGenerals().clear();
		for (ProjectItemGeneral i : gens) {
			p.addGeneral(i);
		}
		p.getPersonnels().clear();
		for (ProjectItemPersonnel i : pers) {
			p.addPersonnel(i);
		}
		
		if (p.isWithWithout()) {
			p.getGeneralWithouts().clear();
			for (ProjectItemGeneralWithout i : gensWo) {
				p.addGeneralWithout(i);
			}
			p.getPersonnelWithouts().clear();
			for (ProjectItemPersonnelWithout i : persWo) {
				p.addPersonnelWithout(i);
			}
		}
		
		storeProject(p, p.getWizardStep()==null);
	}
	
	public void replaceProjectInvest(int projectId, List<ProjectItemAsset> assets, List<ProjectItemLabour> labours, List<ProjectItemService> services, List<ProjectItemAssetWithout> assetsWo, List<ProjectItemLabourWithout> laboursWo, List<ProjectItemServiceWithout> servicesWo) {
		Project p = getProject(projectId, 7, true);
		p.getAssets().clear();
		for (ProjectItemAsset i : assets) {
			p.addAsset(i);
		}
		
		p.getLabours().clear();
		for (ProjectItemLabour i : labours) {
			p.addLabour(i);
		}
		
		p.getServices().clear();
		for (ProjectItemService i : services) {
			p.addService(i);
		}
		
		if (p.isWithWithout()) {
			p.getAssetsWithout().clear();
			for (ProjectItemAssetWithout i : assetsWo) {
				p.addAssetWithout(i);
			}
			p.getLaboursWithout().clear();
			for (ProjectItemLabourWithout i : laboursWo) {
				p.addLabourWithout(i);
			}
			p.getServicesWithout().clear();
			for (ProjectItemServiceWithout i : servicesWo) {
				p.addServiceWithout(i);
			}
		}
		
		storeProject(p, p.getWizardStep()==null);
	}
	
	public void replaceProfileInvest(int profileId, List<ProfileItemGood> goods, List<ProfileItemLabour> labours, List<ProfileItemGoodWithout> goodsWo, List<ProfileItemLabourWithout> laboursWo) {
//		deleteCollections(new String[] {"ProfileItemGood","ProfileItemLabour","ProfileItemGoodWithout","ProfileItemLabourWithout"}, "profile.profileId", profileId);
		
		Profile p = getProfile(profileId, 4);
		p.getGlsGoods().clear();
		for (ProfileItemGood good : goods) {
			p.addGlsGood(good);
		}
		p.getGlsLabours().clear();
		for (ProfileItemLabour labour : labours) {
			p.addGlsLabour(labour);
		}
		
		if (p.getWithWithout()) {
			p.getGlsGoodsWithout().clear();
			for (ProfileItemGoodWithout good : goodsWo) {
				p.addGlsGoodWithout(good);
			}
			p.getGlsLaboursWithout().clear();
			for (ProfileItemLabourWithout labour : laboursWo) {
				p.addGlsLabourWithout(labour);
			}
		}
		
		storeProfile(p, p.getWizardStep()==null);
	}
	
	public Version getLatestVersion() {
		Criteria c = currentSession().createCriteria(Version.class)
				.addOrder(Order.desc("version"))
				.setFirstResult(0).setMaxResults(1);
		return (Version) c.uniqueResult();
	}
	public void storeVersion(Version v) {
		currentSession().saveOrUpdate(v);
	}

	/*
	 * Checks if appConfig objects of ANY OTHER CLASS hold any of the following ids.
	 * This is useful when importing rivConfig objects and providing proper error messages.
	 */
	public boolean isIdsUsedByOtherClasses(Class<? extends AppConfig> clazz, Set<Integer> ids) {
		if (ids.size()==0) return false;
		Criteria c = currentSession().createCriteria(AppConfig.class).add(Restrictions.in("configId", ids));
		@SuppressWarnings("unchecked")
		List<AppConfig> list = c.list();
		boolean fromOtherClass=false;
		for (AppConfig ac : list) {
			if (ac.getClass()!=clazz) {
				fromOtherClass=true;
			}
		}
		return fromOtherClass;
	}
	
	public boolean attachedFileExistsWithFilename(int id, boolean isProject, String filename) {
		Criteria c = isProject ? currentSession().createCriteria(ProjectFile.class)
				: currentSession().createCriteria(ProfileFile.class);
		
		return ((Long)c.add(Restrictions.eq("probaseId", id)).add(Restrictions.eq("filename", filename))
				.setProjection(Projections.rowCount()).uniqueResult())>0;
	}
	
	public void copyAttachedFiles(Probase source, Probase target) {
		String type1 = source.isProject() ? "project" : "profile";
		String type2 = target.isProject() ? "project" : "profile";
		String sql = "insert into "+type2+"_file ("+type2+"_id, filename, length, content) " +
				"select "+target.getProId()+", filename, length, content from "+type1+"_file " +
						"where "+type1+"_id="+source.getProId();
		SQLQuery q = currentSession().createSQLQuery(sql);
		q.executeUpdate();
	}
	
	@SuppressWarnings("unchecked")
	public List<AttachedFile> getAttachedFiles(int id, boolean isProject, boolean loadContentBytes) {
		List<AttachedFile> files = new ArrayList<AttachedFile>();
		if (isProject) {
			files = (List<AttachedFile>)currentSession()
					.createCriteria(ProjectFile.class).add(Restrictions.eq("probaseId", id)).list();
		} else {
			files = (List<AttachedFile>)currentSession()
			.createCriteria(ProfileFile.class).add(Restrictions.eq("probaseId", id)).list();
		}
		
		if (loadContentBytes) {
			for (AttachedFile af : files) {
				try {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					InputStream is = af.getContent().getBinaryStream(); 
					IOUtils.copy(is, baos);
					af.setContentBytes(baos.toByteArray());
					af.getContent().free();
				} catch (Exception e) {
					LOG.error("Error getting content from attached file.",e);
				}
			}
		}
		return files;
	}
	
	public void addAttachedFile(byte[] filebytes, AttachedFile pf) {
		Blob blob = Hibernate.getLobCreator(currentSession()).createBlob(filebytes);
		pf.setContent(blob);
		currentSession().save(pf);
	}
	public void deleteAttachedFile(int id, boolean isProject) {
		String sql = isProject ? "delete from ProjectFile where id=:id" 
				:"delete from ProfileFile where id=:id";
		Query q = currentSession()
				.createQuery(sql).setInteger("id", id);
		q.executeUpdate();
	}
	
	public void streamAttachedFileContent(int id, OutputStream out, boolean isProject) {
		AttachedFile af;
		if (isProject) {
			af = (AttachedFile)currentSession()
			.createCriteria(ProjectFile.class).add(Restrictions.eq("id", id))
			.uniqueResult();
		} else {
			af = (AttachedFile)currentSession()
					.createCriteria(ProfileFile.class).add(Restrictions.eq("id", id))
					.uniqueResult();
		}
		try {
			InputStream is = af.getContent().getBinaryStream(); 
			IOUtils.copy(is, out);
		} catch (Exception e) {
			LOG.error("Error getting file content.",e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<ProfileResult> getProfileResults(FilterCriteria filter) {
		Criteria criteria = currentSession()
				.createCriteria(ProfileResult.class)
				.setMaxResults(100)
				.add(Restrictions.eq("incomeGen", filter.isIncomeGen()))
				.add(Restrictions.like("profileName",
						"%" + filter.getFreeText() + "%").ignoreCase());
		if (filter.getStatuses() != null && filter.getStatuses().size() > 0) {
			criteria.add(Restrictions.in("status", filter.getStatuses()));
		}
		if (filter.getOffices() != null && filter.getOffices().size() > 0) {
			criteria.add(Restrictions.in("fieldOffice", filter.getOffices()));
		}
		if (filter.getUsers() != null && filter.getUsers().size() > 0) {
			criteria.add(Restrictions.in("technician", filter.getUsers()));
		}
		return (List<ProfileResult>) criteria.list();
	}

	@SuppressWarnings("unchecked")
	//TODO: add technician and status to criteria
	public List<Profile> getProfileUnfinished(boolean incomeGen) {
		Criteria criteria = currentSession()
				.createCriteria(Profile.class)
				.add(Restrictions.isNotNull("wizardStep"))
				.add(Restrictions.eq("incomeGen", incomeGen));
		return (List<Profile>) criteria.list();
	}

	public Profile getProfileByUniqueId(byte[] uid) {
		Criteria criteria = currentSession()
				.createCriteria(Profile.class)
				.add(Restrictions.eq("uniqueId", uid));
		return (Profile) criteria.uniqueResult();
	}
	public Project getProjectByUniqueId(byte[] uid) {
		Criteria criteria = currentSession().createCriteria(Project.class).add(Restrictions.eq("uniqueId", uid));
		return (Project) criteria.uniqueResult();
	}

	public Profile getProfile(int id, int step) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Profile.class).add(Restrictions.eq("profileId", id));
		Profile p = (Profile)criteria.uniqueResult();
		if (p!=null) {
			if (step==1 || step==-1) {
				Hibernate.initialize(p.getFieldOffice());
				Hibernate.initialize(p.getStatus());
			}
			if (step==-1 || step==1 || step==4 || step==5 || step==6 || step==7) { // ref costs
				Hibernate.initialize(p.getRefCosts());
			}
			if (step==-1 || step==1 || step==4 || step==6 || step==7) { // ref labours
				Hibernate.initialize(p.getRefLabours());
			}
			if (step==-1 || step==1 || step==6 || step==7) { // ref income
				Hibernate.initialize(p.getRefIncomes());
			}
			if (step==4 || step==-1 || step==1) {
				Hibernate.initialize(p.getGlsGoods());
				Hibernate.initialize(p.getGlsLabours());
				Hibernate.initialize(p.getGlsGoodsWithout());
				Hibernate.initialize(p.getGlsLaboursWithout());
			}
			if (step==5 || step==-1 || step==1) {
				Hibernate.initialize(p.getGlsGeneral());
				Hibernate.initialize(p.getGlsGeneralWithout());
			}
			if (step==1 || step==6 || step==-1) {
				for (ProfileProduct pp : p.getProducts()) {
					Hibernate.initialize(pp.getProfileIncomes());
					Hibernate.initialize(pp.getProfileInputs());
					Hibernate.initialize(pp.getProfileLabours());
				}
				for (ProfileProductWithout pp : p.getProductsWithout()) {
					Hibernate.initialize(pp.getProfileIncomes());
					Hibernate.initialize(pp.getProfileInputs());
					Hibernate.initialize(pp.getProfileLabours());
				}
			}
			if (step==8) {
				Hibernate.initialize(p.getTechnician());
			}
		}
		return p;
	}

	public void storeProfile(Profile p, boolean calculateResult) {
		p.setLastUpdate(new Date());
		currentSession().saveOrUpdate(p);
		if (calculateResult)
			storeProfileResult(p.getProfileId());
	}

	public void storeProfileResult(Integer id) {
		Profile profile = getProfile(id, -1);
		ProfileResult pr = profile.getProfileResult();
		currentSession().saveOrUpdate(pr);
	}

	public void deleteProfile(Profile p) {
		if (p.getWizardStep() == null) {
			ProfileResult pr = getProfileResult(p.getProfileId());
			currentSession().delete(pr);
		}
		currentSession().createQuery("delete from ProfileFile where probaseId=:id").setInteger("id", p.getProfileId()).executeUpdate();
		currentSession().delete(p);
	}

	public ProfileResult getProfileResult(int id) {
		Criteria criteria = currentSession()
				.createCriteria(ProfileResult.class)
				.add(Restrictions.eq("profileId", id));
		return (ProfileResult) criteria.uniqueResult();
	}

	public ProfileItem getProfileItem(int id) {
		Criteria criteria = currentSession()
				.createCriteria(ProfileItem.class)
				.add(Restrictions.eq("profItemId", id));
		ProfileItem item = (ProfileItem) criteria.uniqueResult();
		if (item instanceof ProfileItemGood) {
			Hibernate.initialize(item.getProfile().getGlsGoods());
			Hibernate.initialize(item.getProfile().getRefCosts());
		} else if (item instanceof ProfileItemGoodWithout) {
			Hibernate.initialize(item.getProfile().getGlsGoodsWithout());
			Hibernate.initialize(item.getProfile().getRefCosts());
		} else if (item instanceof ProfileItemLabour) {
			Hibernate.initialize(item.getProfile().getGlsLabours());
			Hibernate.initialize(item.getProfile().getRefLabours());
		} else if (item instanceof ProfileItemLabourWithout) {
			Hibernate.initialize(item.getProfile().getGlsLaboursWithout());
			Hibernate.initialize(item.getProfile().getRefLabours());
		} else if (item instanceof ProfileItemGeneral) {
			Hibernate.initialize(item.getProfile().getGlsGeneral());
			Hibernate.initialize(item.getProfile().getRefCosts());
		} else {
			Hibernate.initialize(item.getProfile().getGlsGeneralWithout());
			Hibernate.initialize(item.getProfile().getRefCosts());
		}
		return item;
	}

	public void storeProfileItem(ProfileItem pi) {
		currentSession().saveOrUpdate(pi);
		if (pi.getProfile().getWizardStep() == null)
			storeProfileResult(pi.getProfile().getProfileId());
	}

	public void deleteProfileItem(ProfileItem pi) {
		Profile p = pi.getProfile();
		int pid = pi.getProfile().getProfileId();
		int orderBy = pi.getOrderBy();
		currentSession().delete(pi);
		genericReorder(pi.getClass().getSimpleName(), "profile_id", pid,
				orderBy);
		if (p.getWizardStep() == null) {
			storeProfileResult(p.getProfileId());
		}
	}

	public ProfileProductBase getProfileProduct(int id, String collectionToInizialize) {
		Criteria criteria = currentSession()
				.createCriteria(ProfileProductBase.class)
				.add(Restrictions.eq("productId", id));
		ProfileProductBase pp = (ProfileProductBase) criteria.uniqueResult();
		if (collectionToInizialize!=null) {
			if (collectionToInizialize.equals("income") || collectionToInizialize.equals("all")) {
				Hibernate.initialize(pp.getProfileIncomes());
				Hibernate.initialize(pp.getProfile().getRefIncomes());
			}  
			if (collectionToInizialize.equals("input") || collectionToInizialize.equals("all")) {
				Hibernate.initialize(pp.getProfileInputs());
				Hibernate.initialize(pp.getProfile().getRefCosts());
				
			} 
			if (collectionToInizialize.equals("labour") || collectionToInizialize.equals("all")) {
				Hibernate.initialize(pp.getProfileLabours());
				Hibernate.initialize(pp.getProfile().getRefLabours());
			}
		}
		return pp;
	}

	public void storeProfileProduct(ProfileProductBase pp) {
		currentSession().saveOrUpdate(pp);
		if (pp.getProfile().getWizardStep() == null) {
			storeProfileResult(pp.getProfile().getProfileId());
		}
	}

	public void deleteProfileProduct(ProfileProductBase pp) {
		Profile p = pp.getProfile();
		int pid = pp.getProfile().getProfileId();
		int orderBy = pp.getOrderBy();
		currentSession().delete(pp);
		genericReorder(pp.getClass().getSimpleName(), "profile_id", pid,
				orderBy);
		if (p.getWizardStep() == null) {
			storeProfileResult(p.getProfileId());
		}
	}

	public ProfileProductItem getProfileProductItem(int id) {
		Criteria criteria = currentSession()
				.createCriteria(ProfileProductItem.class)
				.add(Restrictions.eq("prodItemId", id));
		ProfileProductItem pi = (ProfileProductItem) criteria.uniqueResult();
		if (pi.getClass().isAssignableFrom(ProfileProductIncome.class)) {
			Hibernate.initialize(pi.getProfileProduct().getProfileIncomes());
			Hibernate.initialize(pi.getProbase().getRefIncomes());
		} else if (pi.getClass().isAssignableFrom(ProfileProductInput.class)) {
			Hibernate.initialize(pi.getProfileProduct().getProfileInputs());
			Hibernate.initialize(pi.getProbase().getRefCosts());
		} else {
			Hibernate.initialize(pi.getProfileProduct().getProfileLabours());
			Hibernate.initialize(pi.getProbase().getRefLabours());
		}
		return pi;
	}

	public void storeProfileProductItem(ProfileProductItem pi) {
		currentSession().saveOrUpdate(pi);
		if (pi.getProfileProduct().getProfile().getWizardStep() == null) {
			storeProfileResult(pi.getProfileProduct().getProfile().getProfileId());
		}
	}

	public void deleteProfileProductItem(ProfileProductItem pi) {
		Profile p = pi.getProfileProduct().getProfile();
		int orderBy = pi.getOrderBy();
		currentSession().delete(pi);
		genericReorder(pi.getClass().getSimpleName(), "product_id",
				p.getProfileId(), orderBy);
		if (p.getWizardStep() == null) {
			storeProfileResult(p.getProfileId());
		}
	}

	@SuppressWarnings("unchecked")
	public List<ProjectResult> getProjectResults(FilterCriteria filter) {
		Criteria criteria = currentSession()
				.createCriteria(ProjectResult.class)
				.setMaxResults(100)
				.add(Restrictions.eq("incomeGen", filter.isIncomeGen()))
				.add(Restrictions.or(
						Restrictions.like("projectName",
								"%" + filter.getFreeText() + "%").ignoreCase(),
						Restrictions.like("userCode",
								"%" + filter.getFreeText() + "%").ignoreCase()));
		if (filter.getStatuses() != null && filter.getStatuses().size() > 0) {
			criteria.add(Restrictions.in("status", filter.getStatuses()));
		}
		if (filter.getOffices() != null && filter.getOffices().size() > 0) {
			criteria.add(Restrictions.in("fieldOffice", filter.getOffices()));
		}
		if (filter.getUsers() != null && filter.getUsers().size() > 0) {
			criteria.add(Restrictions.in("technician", filter.getUsers()));
		}
		if (filter.getCategories() != null && filter.getCategories().size() > 0) {
			criteria.add(Restrictions.in("projCategory", filter.getCategories()));
		}
		if (filter.getBeneficiaries() != null && filter.getBeneficiaries().size() > 0) {
			criteria.add(Restrictions.in("beneficiary",
					filter.getBeneficiaries()));
		}
		if (filter.getAppConfig1s() != null && filter.getAppConfig1s().size() > 0) {
			criteria.add(Restrictions.in("appConfig1", filter.getAppConfig1s()));
		}
		if (filter.getAppConfig2s() != null && filter.getAppConfig2s().size() > 0) {
			criteria.add(Restrictions.in("appConfig2", filter.getAppConfig2s()));
		}
		if (filter.getEnviroCategories() != null && filter.getEnviroCategories().size() > 0) {
			criteria.add(Restrictions.in("enviroCategory",
					filter.getEnviroCategories()));
		}
		if (filter.getIrrCriteria() != 0) {
			switch (filter.getIrrCriteria()) {
			case 1:
				criteria.add(Restrictions.eq("irr", filter.getIrrValue()
						.divide(BigDecimal.valueOf(100.0))));
				break;
			case 2:
				criteria.add(Restrictions.ge("irr", filter.getIrrValue()
						.divide(BigDecimal.valueOf(100.0))));
				break;
			case 3:
				criteria.add(Restrictions.le("irr", filter.getIrrValue()
						.divide(BigDecimal.valueOf(100.0))));
			}
		}
		if (filter.getNpvCriteria() != 0) {
			switch (filter.getNpvCriteria()) {
			case 1:
				criteria.add(Restrictions.eq("npv", filter.getNpvValue()));
				break;
			case 2:
				criteria.add(Restrictions.ge("npv", filter.getNpvValue()));
				break;
			case 3:
				criteria.add(Restrictions.le("npv", filter.getNpvValue()));
			}
		}
		if (filter.getTotInvestCriteria() != 0) {
			switch (filter.getTotInvestCriteria()) {
			case 1:
				criteria.add(Restrictions.eq("investmentTotal",
						filter.getTotInvestValue()));
				break;
			case 2:
				criteria.add(Restrictions.ge("investmentTotal",
						filter.getTotInvestValue()));
				break;
			case 3:
				criteria.add(Restrictions.le("investmentTotal",
						filter.getTotInvestValue()));
			}
		}
		if (filter.getExternCriteria() != 0) {
			switch (filter.getExternCriteria()) {
			case 1:
				criteria.add(Restrictions.eq("investmentDonated",
						filter.getExternValue()));
				break;
			case 2:
				criteria.add(Restrictions.ge("investmentDonated",
						filter.getExternValue()));
				break;
			case 3:
				criteria.add(Restrictions.le("investmentDonated",
						filter.getExternValue()));
			}
		}
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<Project> getProjectUnfinished(boolean incomeGen) {
		Criteria criteria = currentSession()
				.createCriteria(Project.class)
				.add(Restrictions.isNotNull("wizardStep"))
				.add(Restrictions.eq("incomeGen", incomeGen));
		return (List<Project>) criteria.list();
	}
	
	public ProjectResult getProjectResult(int id) {
		Criteria criteria = currentSession()
				.createCriteria(ProjectResult.class)
				.add(Restrictions.eq("projectId", id));
		try {
			return (ProjectResult) criteria.uniqueResult();
		} catch (Exception e) {
			return null;
		}
	}
	
	public Donor getDonor(int id) {
		Criteria criteria = currentSession().createCriteria(Donor.class).add(Restrictions.eq("id", id));
		Donor d = (Donor)criteria.uniqueResult();
		Hibernate.initialize(d.getProject());
		return d;
	}
	public void storeDonor(Donor d) {
		currentSession().saveOrUpdate(d);
	}
	public void deleteDonor(Donor d) {
		int projId = d.getProject().getProjectId();
		int orderBy = d.getOrderBy();
		
		String sql = "UPDATE project_item_donation pid SET pid.donor_order_by=pid.donor_order_by-1 "+
				"WHERE pid.item_id IN (SELECT pi.proj_item_id FROM project_item pi WHERE pi.project_id=:projectId) AND pid.donor_order_by>:orderBy";
		SQLQuery q = currentSession().createSQLQuery(sql);
		q.setInteger("projectId", projId);
		q.setInteger("orderBy", orderBy);
		q.executeUpdate();
		
		sql = "UPDATE project_item SET donor_order_by=donor_order_by-1 WHERE project_id=:projectId AND donor_order_by>:orderBy";
		q = currentSession().createSQLQuery(sql);
		q.setInteger("projectId", projId);
		q.setInteger("orderBy", orderBy);
		q.executeUpdate();
		
		sql = "UPDATE project_block_item_donation bid SET bid.donor_order_by=bid.donor_order_by-1 "+
				"WHERE bid.block_item_id IN (SELECT bi.prod_item_id FROM project_block_item bi JOIN project_block b ON b.block_id=bi.block_id WHERE b.project_id=:projectId) "+
				"AND bid.donor_order_by>:orderBy";
		q = currentSession().createSQLQuery(sql);
		q.setInteger("projectId", projId);
		q.setInteger("orderBy", orderBy);
		q.executeUpdate();
		
		currentSession().delete(d);
		genericReorder(d.getClass().getSimpleName(), "project_id", projId, orderBy);
	}

	public Project getProject(int id, int step) {
		return getProject(id, step, false);
	}
	
	public Project getProject(int id, int step, boolean withDonations) {
		Criteria criteria = currentSession()
				.createCriteria(Project.class)
				.add(Restrictions.eq("projectId", id));
		Project p = (Project) criteria.uniqueResult();
		if (p==null) { return p; }
		
		if (!p.getIncomeGen()&&step==10) {withDonations=true;}
		
		// only populate collections necessary for the operation performed
		Hibernate.initialize(p.getTechnician());

		if (step==-1 || step==1 || step==13) {
			Hibernate.initialize(p.getFieldOffice());
			Hibernate.initialize(p.getStatus());
			Hibernate.initialize(p.getBeneficiary());
			Hibernate.initialize(p.getEnviroCategory());
			Hibernate.initialize(p.getProjCategory());
			Hibernate.initialize(p.getAppConfig1());
			Hibernate.initialize(p.getAppConfig2());
		}
		if (step==-1 || step==1 || step==2  || step==7 || step==12 || step==13
				|| (!p.getIncomeGen() && (step==8 || step==10 ))
			) {
			Hibernate.initialize(p.getDonors());
		}
		if (step==-1 || step==7 || step==12 || step==13
				|| (!p.getIncomeGen()&&step==10) || (p.getIncomeGen()&&step==11)
				) {
			Hibernate.initialize(p.getAssets());
			Hibernate.initialize(p.getLabours());
			Hibernate.initialize(p.getServices());
		}
		
		if (step==-1 || step==1 || step==7 || step==12 || step==13
				|| (!p.getIncomeGen()&&step==10) || (p.getIncomeGen()&&step==11)
				) {
			Hibernate.initialize(p.getAssetsWithout());
			Hibernate.initialize(p.getLaboursWithout());
			Hibernate.initialize(p.getServicesWithout());
		}
		
		if (step==-1) {
			Hibernate.initialize(p.getLaboursFromProfile());
			Hibernate.initialize(p.getLaboursFromProfileWithout());
			Hibernate.initialize(p.getGeneralsFromProfile());
			Hibernate.initialize(p.getGeneralsFromProfileWithout());
		}
		
		if (step==-1 || step==7 || step==8 || step==13 
				|| ((p.getIncomeGen() && step==10) || (!p.getIncomeGen() && step==11))) {
			Hibernate.initialize(p.getRefCosts());
			Hibernate.initialize(p.getRefLabours());
		}
		if (step==-1 || (p.getIncomeGen() && step==10) || (!p.getIncomeGen() && step==11)) {
			Hibernate.initialize(p.getRefIncomes());
		}
		
		if (p.getIncomeGen() && (step==-1 || step==1 || step==8 || step==11 || step==12 || step==13)) {
			Hibernate.initialize(p.getGenerals());
			for (ProjectItemGeneralBase b : p.getGenerals()) {
				Hibernate.initialize(b.getYears());
			}
			Hibernate.initialize(p.getGeneralWithouts());
			for (ProjectItemGeneralBase b : p.getGeneralWithouts()) {
				Hibernate.initialize(b.getYears());
			}
			
			Hibernate.initialize(p.getPersonnels());
			for (ProjectItemGeneralBase b : p.getPersonnels()) {
				Hibernate.initialize(b.getYears());
			}
			Hibernate.initialize(p.getPersonnelWithouts());
			for (ProjectItemGeneralBase b : p.getPersonnelWithouts()) {
				Hibernate.initialize(b.getYears());
			}
		}
		
		if (!p.getIncomeGen() && (step==-1 || step==8 || step==10 || step==12 || step==13)) {
			Hibernate.initialize(p.getNongenLabours());
			Hibernate.initialize(p.getNongenMaintenance());
			Hibernate.initialize(p.getNongenMaterials());
		}
	
		if (step==-1 || step==1 || step==9 || step==12 || step==13
				|| (p.getIncomeGen() && step==11)
				|| (!p.getIncomeGen() && step==10)
				) {
			for (Block b : p.getBlocks()) {
				Hibernate.initialize(b.getChrons());
				Hibernate.initialize(b.getPatterns());
				Hibernate.initialize(b.getIncomes());
				Hibernate.initialize(b.getInputs());
				Hibernate.initialize(b.getLabours());
			}
			for (BlockWithout b : p.getBlocksWithout()) {
				Hibernate.initialize(b.getChrons());
				Hibernate.initialize(b.getPatterns());
				Hibernate.initialize(b.getIncomes());
				Hibernate.initialize(b.getInputs());
				Hibernate.initialize(b.getLabours());
			}
		}
		
		if (!p.getIncomeGen() && (step==-1 || step==1 || step==10 || step==12 || step==13)) {
			Hibernate.initialize(p.getContributions());
			for (ProjectItemContribution c : p.getContributions()) {
				Hibernate.initialize(c.getYears());
			}
		}
		
		if (withDonations || step==12 || step==13) {
			for (HasDonations i : p.getAssets()) {
				Hibernate.initialize(i.getDonations());
			}
			for (HasDonations i : p.getAssetsWithout()) {
				Hibernate.initialize(i.getDonations());
			}
			for (HasDonations i : p.getLabours()) {
				Hibernate.initialize(i.getDonations());
			}
			for (HasDonations i : p.getLaboursWithout()) {
				Hibernate.initialize(i.getDonations());
			}
			for (HasDonations i : p.getServices()) {
				Hibernate.initialize(i.getDonations());
			}
			for (HasDonations i : p.getServicesWithout()) {
				Hibernate.initialize(i.getDonations());
			}
			for (HasDonations i : p.getNongenMaterials()) {
				Hibernate.initialize(i.getDonations());
			}
			for (HasDonations i : p.getNongenLabours()) {
				Hibernate.initialize(i.getDonations());
			}
			for (HasDonations i : p.getNongenMaintenance()) {
				Hibernate.initialize(i.getDonations());
			}
			for (BlockBase b : p.getBlocks()) {
				for (HasDonations i : b.getInputs()) {
					Hibernate.initialize(i.getDonations());
				}
				for (HasDonations i : b.getLabours()) {
					Hibernate.initialize(i.getDonations());
				}
			}
			for (BlockBase b : p.getBlocksWithout()) {
				for (HasDonations i : b.getInputs()) {
					Hibernate.initialize(i.getDonations());
				}
				for (HasDonations i : b.getLabours()) {
					Hibernate.initialize(i.getDonations());
				}
			}
		}
		
		return p;
	}
	
	public void storeProject(Project project, boolean storeResult) {
		project.setLastUpdate(new java.util.Date());
		currentSession().saveOrUpdate(project);
		if (storeResult) { 
			storeProjectResult(project.getProjectId()); 
		}
	}
	
	public void deleteProjectResult(int id) {
		currentSession().createQuery("delete from ProjectResult where projectId=:id").setInteger("id", id).executeUpdate();
	}
	
	public void deleteProfileResult(int id) {
		currentSession().createQuery("delete from ProfileResult where profileId=:id").setInteger("id", id).executeUpdate();
	}
	
	public void deleteProject(Project p) {
		if (p.getWizardStep() == null) {
			deleteProjectResult(p.getProjectId());
		}
		currentSession().createQuery("delete from ProjectFile where probaseId=:id").setInteger("id", p.getProjectId()).executeUpdate();
		currentSession().delete(p);
	}
	
	public BlockBase getBlock(int id, String fetchCollection) {
		BlockBase b = (BlockBase)currentSession().createCriteria(BlockBase.class).add(Restrictions.eq("id", id)).uniqueResult();
		
		if (fetchCollection==null) {// || fetchCollection.equals("all")) {
			Hibernate.initialize(b.getPatterns());
			Hibernate.initialize(b.getChrons());
		} else {
			if (fetchCollection.equals("income") || fetchCollection.equals("all")) {
				Hibernate.initialize(b.getIncomes());
				Hibernate.initialize(b.getProject().getRefIncomes());
			} 
			if (fetchCollection.equals("input") || fetchCollection.equals("all")) {
				Hibernate.initialize(b.getInputs());
				for (BlockInput bi : b.getInputs()) {
					Hibernate.initialize(bi.getDonations());
				}
				Hibernate.initialize(b.getProject().getRefCosts());
			} 
			if (fetchCollection.equals("labour")  || fetchCollection.equals("all")) {
				Hibernate.initialize(b.getLabours());
				for (BlockLabour bi : b.getLabours()) {
					Hibernate.initialize(bi.getDonations());
				}
				Hibernate.initialize(b.getProject().getRefLabours());
			}
			if (fetchCollection.equals("all")) {
				Hibernate.initialize(b.getPatterns());
				Hibernate.initialize(b.getChrons());
			}
			Hibernate.initialize(b.getProject().getDonors());
		}
		
		if (b.getClass()==BlockWithout.class) {
			Hibernate.initialize(b.getProject().getBlocks());
		}
		return b;
	}
	public void storeBlock(BlockBase b) {
		currentSession().saveOrUpdate(b);
		if (b.getProject().getWizardStep()==null) {
			storeProjectResult(b.getProject().getProjectId());
		}
	}
	public void deleteBlock(BlockBase block) {
		int pid = block.getProject().getProjectId();
		int orderBy = block.getOrderBy();
		currentSession().delete(block);
		genericReorder(block.getClass().getSimpleName(), "project_id", pid, orderBy);
	}
	public void movBlock(BlockBase block, boolean up) {
		genericMoveOrder(block.getClass().getSimpleName(), "block_id", block.getBlockId(), "project_id", block.getProject().getProjectId(), block.getOrderBy(), up);
	}

	public BlockItem getBlockItem(int id) {
		BlockItem bi = (BlockItem)currentSession().createCriteria(BlockItem.class).add(Restrictions.eq("prodItemId", id)).uniqueResult();
		
		if (bi instanceof HasDonations) {
			Hibernate.initialize(bi.getBlock().getProject().getDonors());
			Hibernate.initialize(((HasDonations)bi).getDonations());
		}
		
		if (bi.getClass().isAssignableFrom(BlockIncome.class)) {
			Hibernate.initialize(bi.getBlock().getProject().getRefIncomes());
			Hibernate.initialize(bi.getBlock().getIncomes());
		} else if (bi.getClass().isAssignableFrom(BlockInput.class)) {
			Hibernate.initialize(bi.getBlock().getProject().getRefCosts());
			Hibernate.initialize(bi.getBlock().getInputs());
		} else if (bi.getClass().isAssignableFrom(BlockLabour.class)) {
			Hibernate.initialize(bi.getBlock().getProject().getRefLabours());
			Hibernate.initialize(bi.getBlock().getLabours());
		}
		
		return bi;
	}
	public void storeBlockItem(BlockItem bi) {
		currentSession().saveOrUpdate(bi);
		if (bi.getBlock().getProject().getWizardStep()==null) {
			storeProjectResult(bi.getBlock().getProject().getProjectId());
		}
	}
	public void deleteBlockItem(BlockItem item) {
		int orderBy = item.getOrderBy();
		int blockId = item.getBlock().getBlockId();
		currentSession().delete(item);
		genericReorder(item.getClass().getSimpleName(), "block_id", blockId, orderBy);
	}
	
	public void checkProfilesOnUpgrade() {
		Query q = currentSession().createQuery("select p.profile.profileId from ProfileProduct p where p.orderBy is null group by p.profile.profileId");
		for (Object pId : q.list()) {
			Profile p = getProfile((Integer)pId, 6);
			p.correctBlockOrderProblem();
			currentSession().save(p);
		}
		
		q = currentSession().createQuery("select g.profile.profileId from ProfileItemGood g where g.orderBy is null group by g.profile.profileId");
		for (Object pId : q.list()) {
			Profile p = getProfile((Integer)pId, 6);
			p.addOrders();
			currentSession().save(p);
		}
		
		// createdBy is null
		q = currentSession().createQuery("select p.profileId from Profile p where p.createdBy is null");
		for (Object pId : q.list()) {
			Profile p = getProfile((Integer)pId, 1);
			p.setCreatedBy(p.getTechnician().getDescription() + " ("+p.getTechnician().getOrganization()+")");
			currentSession().save(p);
		}
	}
	
	public void checkProjectsOnUpgrade() {
		// RIV<2.0 blocks missing orderBy field 
		// RIV<4.0 blocks orderBy sequence is corrupted (without blocks moved to different collection)
		String sql = 
				"SELECT p.project_id FROM project p "+
				"LEFT JOIN project_block b ON (p.project_id=b.project_id AND  b.class='0') "+
				"LEFT OUTER JOIN project_block b2 ON (p.project_id=b2.project_id AND b2.class='0' AND b2.order_by IS NULL) "+
				"GROUP BY p.project_id "+
				"HAVING MAX(b.order_by)<>COUNT(b.block_id)-1  "+
				"OR COUNT(b2.block_id)>0";
		SQLQuery sq = currentSession().createSQLQuery(sql);
		for (Object pId : sq.list()) {
			int id = (Integer)pId;
			sql = "UPDATE project_block SET order_by=ROWNUM()-1 WHERE project_id=:projId AND class='0'";
			sq = currentSession().createSQLQuery(sql);
			sq.setInteger("projId", id);
			sq.executeUpdate();
		}
		
		// project items missing orderBy or orderBy is corrupted
		sql = 
				"SELECT p.project_id, i.class, MAX(i.order_by), COUNT(i.proj_item_id) "+
				"FROM project p LEFT JOIN project_item i ON p.project_id=i.project_id  "+
				"GROUP BY p.project_id, i.class "+
				"HAVING MAX(i.order_by)<>COUNT(i.proj_item_id)-1 OR (MAX(i.order_by) IS NULL AND COUNT(i.proj_item_id)>0)";
		sq = currentSession().createSQLQuery(sql);
		for (Object o : sq.list()) {
			Object[] data = (Object[])o;
			int id = (Integer)data[0];
			String clazz = (String)data[1];
			sql = "UPDATE project_item SET order_by=ROWNUM()-1 WHERE project_id=:id AND class=:class";
			sq = currentSession().createSQLQuery(sql);
			sq.setInteger("id", id);
			sq.setString("class", clazz);
			sq.executeUpdate();
		}
		
		// project block items order by is corrupted
		sql = "select i.block_id, i.class as maks from project_block_item  i "+
				"group by i.block_id, i.class "+
				"having max(i.order_by)<>count(i.prod_item_id)-1";
		sq = currentSession().createSQLQuery(sql);
		for (Object o : sq.list()) {
			Object[] data = (Object[])o;
			int id = (Integer)data[0];
			String clazz = (String)data[1];
			sql = "UPDATE project_block_item SET order_by=ROWNUM()-1 WHERE block_id=:id AND class=:class";
			sq = currentSession().createSQLQuery(sql);
			sq.setInteger("id", id);
			sq.setString("class", clazz);
			sq.executeUpdate();
		}
		
		// createdBy is null
		Query q = currentSession().createQuery("select p.projectId from Project p where p.createdBy is null");
		for (Object pId : q.list()) {
			Project p = getProject((Integer)pId, 1);
			p.setCreatedBy(p.getTechnician().getDescription() + " ("+p.getTechnician().getOrganization()+")");
			currentSession().save(p);
		}
	}
	
	public void simplifyContributions(Project p, boolean simplify) {
		if (simplify) {
			Query query=currentSession().createQuery("delete ProjectItemContributionPerYear year " +
					 "where year.id in (select y.id from ProjectItemContributionPerYear y where y.year>0 and y.parent.project=:p)");
				query.setParameter("p", p);
				query.executeUpdate();
		} else {
			for (ProjectItemContribution c : p.getContributions()) {
				for (int i=1;i<p.getDuration();i++) {
					ProjectItemContributionPerYear newYear = new ProjectItemContributionPerYear();
					newYear.setUnitNum(c.getYears().get(0).getUnitNum());
					newYear.setYear(i);
					newYear.setParent(c);
					c.getYears().put(i, newYear);
					currentSession().save(newYear);
					currentSession().flush();
					currentSession().saveOrUpdate(c);
				}
			}
		}
	}
	
	public void simplifyGeneralCosts(Project p, boolean simplify) {
		if (simplify) { // from per-year to simplified model
			Query query=currentSession().createQuery("delete ProjectItemGeneralPerYear year " +
					"where year.id in (select y.id from ProjectItemGeneralPerYear y where y.year>0 and y.parent.project=:p)");
			query.setParameter("p", p);
			query.executeUpdate();
		} else { // from simplified to per-year model
			MakePerYearGeneralCosts(p.getGenerals());
			MakePerYearGeneralCosts(p.getGeneralWithouts());
			MakePerYearGeneralCosts(p.getPersonnels());
			MakePerYearGeneralCosts(p.getPersonnelWithouts());
		}
	}
	
	private void MakePerYearGeneralCosts(Set<? extends ProjectItemGeneralBase> gens) {
		for (ProjectItemGeneralBase gen : gens) {
			ProjectItemGeneralPerYear y1 = gen.getYears().get(0);
			for (int i=1;i<gen.getProject().getDuration();i++) {
				ProjectItemGeneralPerYear newYear = new ProjectItemGeneralPerYear();
				newYear.setOwnResources(y1.getOwnResources());
				newYear.setUnitNum(y1.getUnitNum());
				newYear.setYear(i);
				newYear.setParent(gen);
				gen.getYears().put(i, newYear);
				currentSession().save(newYear);
				currentSession().flush();
				currentSession().saveOrUpdate(gen);
			}
		}
	}
	
	public void recalculateCompletedProjects() {
		Query q = currentSession()
				.createQuery("select p.projectId from Project p where wizardStep is null");
		for (Object p : q.list()) {
			storeProjectResult((Integer)p);
		}
	}
	
	public void recalculateCompletedProfiles() {
		Query q = currentSession()
				.createQuery("select p.profileId from Profile p where wizardStep is null");
		for (Object p : q.list()) {
			storeProfileResult((Integer)p);
		}
	}
	
	public void updateQualitativeAnalysis(int id, double qa) {
		Query q = currentSession().createQuery("update ProjectResult pr SET qualitative=:qualitative WHERE id=:id");
		q.setInteger("id", id);
		q.setDouble("qualitative", qa);
		q.executeUpdate();
	}

	public void storeProjectResult(int id) {
		Project project = getProject(id, -1);
		Setting setting = getAppSetting();
		ProjectResult pr = project.getProjectResult(setting);
		currentSession().saveOrUpdate(pr);
	}

	public ReferenceItem getReferenceItem(int id) {
		Criteria criteria = currentSession()
				.createCriteria(ReferenceItem.class)
				.add(Restrictions.eq("refItemId", id));
		ReferenceItem ri = (ReferenceItem) criteria.uniqueResult();
		Hibernate.initialize(ri.getProbase().getRefIncomes());
		Hibernate.initialize(ri.getProbase().getRefCosts());
		Hibernate.initialize(ri.getProbase().getRefLabours());
		
		return ri;
	}

	public void storeReferenceItem(ReferenceItem ref) {
		currentSession().saveOrUpdate(ref);
	}
	
	public void updateReferenceLinks(ReferenceItem ref) {
		if (ref.getClass().isAssignableFrom(ReferenceIncome.class)) {
			ReferenceIncome i = (ReferenceIncome)ref;
			String hql = String.format("update %s i set unitType=:unitType, unitCost=:unitCost %s where linkedTo=:linkedTo",
					i.getProbase().isProject() ? "BlockIncome" : "ProfileProductIncome",
					i.getProbase().getIncomeGen() ? ", transport=:transport" : "");
			Query q = currentSession().createQuery(hql);
			q.setString("unitType", ref.getUnitType());
			q.setBigDecimal("unitCost", new BigDecimal(ref.getUnitCost()));
			if (i.getProbase().getIncomeGen()) {
				q.setBigDecimal("transport", new BigDecimal(i.getTransport()));
			}
			q.setParameter("linkedTo", i);
			q.executeUpdate();
			
			if (i.getProbase().getWizardStep()==null) {
				if (i.getProbase().isProject()) {
					storeProjectResult(i.getProject().getProjectId());
				} else {
					storeProfileResult(i.getProfile().getProfileId());
				}
			}
		} else if (ref.getClass().isAssignableFrom(ReferenceLabour.class)) {
			ReferenceLabour i = (ReferenceLabour)ref;
			if (i.getProbase().isProject()) {
				Query q = currentSession().createQuery("update ProjectItemLabour ppi set unitType=:unitType, unitCost=:unitCost where linkedTo=:linkedTo");
				q.setString("unitType", ref.getUnitType());
				q.setDouble("unitCost", ref.getUnitCost());
				q.setParameter("linkedTo", i);
				q.executeUpdate();
				
				q = currentSession().createQuery("update BlockLabour ppi set unitType=:unitType, unitCost=:unitCost where linkedTo=:linkedTo");
				q.setString("unitType", ref.getUnitType());
				q.setBigDecimal("unitCost", new BigDecimal(ref.getUnitCost()));
				q.setParameter("linkedTo", i);
				q.executeUpdate();
			
				if (i.getProject().getIncomeGen()) {
					q = currentSession().createQuery("update ProjectItemPersonnel ppi set unitType=:unitType, unitCost=:unitCost where linkedTo=:linkedTo");
					q.setString("unitType", ref.getUnitType());
					q.setDouble("unitCost", ref.getUnitCost());
					q.setParameter("linkedTo", i);
					q.executeUpdate();
					
					q = currentSession().createQuery("update ProjectItemPersonnelWithout ppi set unitType=:unitType, unitCost=:unitCost where linkedTo=:linkedTo");
					q.setString("unitType", ref.getUnitType());
					q.setDouble("unitCost", ref.getUnitCost());
					q.setParameter("linkedTo", i);
					q.executeUpdate();
				} else {
					q = currentSession().createQuery("update ProjectItemNongenLabour ppi set unitType=:unitType, unitCost=:unitCost where linkedTo=:linkedTo");
					q.setString("unitType", ref.getUnitType());
					q.setDouble("unitCost", ref.getUnitCost());
					q.setParameter("linkedTo", i);
					q.executeUpdate();
				}
				
				if (ref.getProject().getWizardStep()==null) { storeProjectResult(ref.getProject().getProjectId()); }
			} else {
				Query q = currentSession().createQuery("update ProfileProductLabour ppi set unitType=:unitType, unitCost=:unitCost where linkedTo=:linkedTo");
				q.setString("unitType", ref.getUnitType());
				q.setBigDecimal("unitCost", new BigDecimal(ref.getUnitCost()));
				q.setParameter("linkedTo", i);
				q.executeUpdate();
				
				q = currentSession().createQuery("update ProfileItemLabour ppi set unitType=:unitType, unitCost=:unitCost where linkedTo=:linkedTo");
				q.setString("unitType", ref.getUnitType());
				q.setDouble("unitCost", ref.getUnitCost());
				q.setParameter("linkedTo", i);
				q.executeUpdate();
				
				if (ref.getProfile().getWizardStep()==null) { storeProfileResult(ref.getProfile().getProfileId()); }
			}
		} else {// costs
			ReferenceCost i = (ReferenceCost)ref;
			if (i.getProbase().isProject()) {
				Query q = currentSession().createQuery("update ProjectItemAsset ppi set unitType=:unitType, unitCost=:unitCost where linkedTo=:linkedTo");
				q.setString("unitType", ref.getUnitType());
				q.setDouble("unitCost", ref.getUnitCost());
				q.setParameter("linkedTo", i);
				q.executeUpdate();
				
				q = currentSession().createQuery("update ProjectItemService ppi set unitType=:unitType, unitCost=:unitCost where linkedTo=:linkedTo");
				q.setString("unitType", ref.getUnitType());
				q.setDouble("unitCost", ref.getUnitCost());
				q.setParameter("linkedTo", i);
				q.executeUpdate();
				
				q = currentSession().createQuery("update BlockInput ppi set unitType=:unitType, unitCost=:unitCost where linkedTo=:linkedTo");
				q.setString("unitType", ref.getUnitType());
				q.setBigDecimal("unitCost", new BigDecimal(ref.getUnitCost()));
				q.setParameter("linkedTo", i);
				q.executeUpdate();
				
				if (i.getProject().getIncomeGen()) {
					q = currentSession().createQuery("update ProjectItemGeneral ppi set unitType=:unitType, unitCost=:unitCost where linkedTo=:linkedTo");
					q.setString("unitType", ref.getUnitType());
					q.setDouble("unitCost", ref.getUnitCost());
					q.setParameter("linkedTo", i);
					q.executeUpdate();
					
					q = currentSession().createQuery("update ProjectItemGeneralWithout ppi set unitType=:unitType, unitCost=:unitCost where linkedTo=:linkedTo");
					q.setString("unitType", ref.getUnitType());
					q.setDouble("unitCost", ref.getUnitCost());
					q.setParameter("linkedTo", i);
					q.executeUpdate();
					
					q = currentSession().createQuery("update ProjectItemPersonnel ppi set unitType=:unitType, unitCost=:unitCost where linkedTo=:linkedTo");
					q.setString("unitType", ref.getUnitType());
					q.setDouble("unitCost", ref.getUnitCost());
					q.setParameter("linkedTo", i);
					q.executeUpdate();
					
					q = currentSession().createQuery("update ProjectItemPersonnelWithout ppi set unitType=:unitType, unitCost=:unitCost where linkedTo=:linkedTo");
					q.setString("unitType", ref.getUnitType());
					q.setDouble("unitCost", ref.getUnitCost());
					q.setParameter("linkedTo", i);
					q.executeUpdate();
				} else {
					q = currentSession().createQuery("update ProjectItemGeneral ppi set unitType=:unitType, unitCost=:unitCost where linkedTo=:linkedTo");
					q.setString("unitType", ref.getUnitType());
					q.setDouble("unitCost", ref.getUnitCost());
					q.setParameter("linkedTo", i);
					q.executeUpdate();
			
					q = currentSession().createQuery("update ProjectItemNongenMaterials ppi set unitType=:unitType, unitCost=:unitCost where linkedTo=:linkedTo");
					q.setString("unitType", ref.getUnitType());
					q.setDouble("unitCost", ref.getUnitCost());
					q.setParameter("linkedTo", i);
					q.executeUpdate();
					
					q = currentSession().createQuery("update ProjectItemNongenMaintenance ppi set unitType=:unitType, unitCost=:unitCost where linkedTo=:linkedTo");
					q.setString("unitType", ref.getUnitType());
					q.setDouble("unitCost", ref.getUnitCost());
					q.setParameter("linkedTo", i);
					q.executeUpdate();
				}
				if (ref.getProject().getWizardStep()==null) { storeProjectResult(ref.getProject().getProjectId()); }
			} else {
				Query q = currentSession().createQuery("update ProfileItemGood ppi set unitType=:unitType, unitCost=:unitCost where linkedTo=:linkedTo");
				q.setString("unitType", ref.getUnitType());
				q.setDouble("unitCost", ref.getUnitCost());
				q.setParameter("linkedTo", i);
				q.executeUpdate();
				
				q = currentSession().createQuery("update ProfileItemGeneral ppi set unitType=:unitType, unitCost=:unitCost where linkedTo=:linkedTo");
				q.setString("unitType", ref.getUnitType());
				q.setDouble("unitCost", ref.getUnitCost());
				q.setParameter("linkedTo", i);
				q.executeUpdate();
				
				q = currentSession().createQuery("update ProfileProductInput ppi set unitType=:unitType, unitCost=:unitCost where linkedTo=:linkedTo");
				q.setString("unitType", ref.getUnitType());
				q.setBigDecimal("unitCost", new BigDecimal(ref.getUnitCost()));
				q.setParameter("linkedTo", i);
				q.executeUpdate();
				
				if (ref.getProfile().getWizardStep()==null) { storeProfileResult(ref.getProfile().getProfileId()); }
			}
		}
	}
	
	public ProjectItem getProjectItem(int id) {
		ProjectItem item = (ProjectItem)currentSession().createCriteria(ProjectItem.class).add(Restrictions.eq("projItemId", id)).uniqueResult();
		Project p = item.getProject();
		
		if (item instanceof HasDonations) {
			Hibernate.initialize(p.getDonors());
			Hibernate.initialize(((HasDonations)item).getDonations());
		} else if (item instanceof ProjectItemContribution) {
			Hibernate.initialize(p.getDonors());
		}
		
		if (item instanceof ProjectItemContribution) {
			Hibernate.initialize(p.getContributions());
			for (ProjectItemContribution c : p.getContributions()) {
				Hibernate.initialize(c.getYears());
			}
			Hibernate.initialize(item.getProject().getRefIncomes());
		} else if (item instanceof ProjectItemLabour) {
			Hibernate.initialize(p.getRefLabours());
			Hibernate.initialize(p.getLabours());
		} else if (item instanceof ProjectItemLabourWithout) {
			Hibernate.initialize(p.getRefLabours());
			Hibernate.initialize(p.getLaboursWithout());
		} else if (item instanceof ProjectItemPersonnel) {
			Hibernate.initialize(p.getRefLabours());
			Hibernate.initialize(p.getPersonnels());
		} else if (item instanceof ProjectItemPersonnelWithout) {
			Hibernate.initialize(p.getRefLabours());
			Hibernate.initialize(p.getPersonnelWithouts());
		} else if (item instanceof ProjectItemNongenLabour) {
//			Hibernate.initialize(((ProjectItemNongenBase)item).getDonations());
			Hibernate.initialize(item.getProject().getRefLabours());
			Hibernate.initialize(p.getNongenLabours());
		} else {
			Hibernate.initialize(item.getProject().getRefCosts());
			if (item.getClass().isAssignableFrom(ProjectItemAsset.class)) {
				Hibernate.initialize(p.getAssets());
			} else if (item.getClass().isAssignableFrom(ProjectItemAssetWithout.class)) {
				Hibernate.initialize(p.getAssetsWithout());
			} else if (item.getClass().isAssignableFrom(ProjectItemGeneral.class)) {
				Hibernate.initialize(p.getGenerals());
			} else if (item.getClass().isAssignableFrom(ProjectItemGeneralWithout.class)) {
				Hibernate.initialize(p.getGeneralWithouts());
			} else if (item.getClass().isAssignableFrom(ProjectItemService.class)) {
				Hibernate.initialize(p.getServices());
			} else if (item.getClass().isAssignableFrom(ProjectItemServiceWithout.class)) {
				Hibernate.initialize(p.getServicesWithout());
			} else if (item.getClass().isAssignableFrom(ProjectItemNongenMaterials.class)) {
//				Hibernate.initialize(((ProjectItemNongenBase)item).getDonations());
				Hibernate.initialize(p.getNongenMaterials());
			} else if (item.getClass().isAssignableFrom(ProjectItemNongenMaintenance.class)) {
//				Hibernate.initialize(((ProjectItemNongenBase)item).getDonations());
				Hibernate.initialize(p.getNongenMaintenance());
			}
		}
		
		if (item instanceof ProjectItemGeneralBase) {
			Hibernate.initialize(((ProjectItemGeneralBase)item).getYears());
		}
		return item;
	}
	public void storeProjectItem(ProjectItem pi) {
		storeProjectItem(pi, false);
	}
	public void storeProjectItem(ProjectItem pi, boolean noResult) {
		currentSession().saveOrUpdate(pi);
		if (!noResult && pi.getProject().getWizardStep()==null) {
			storeProjectResult(pi.getProject().getProjectId());
		}
	}
	
	public void deleteProjectItem(ProjectItem item) {
		int projId = item.getProject().getProjectId();
		int orderBy = item.getOrderBy();
		
		currentSession().delete(item);
		
//		if (item.getClass().isAssignableFrom(ProjectItemContribution.class)) {
//			genericReorderWithYear(item.getClass().getSimpleName(), "project_id", projId, orderBy, ((ProjectItemContribution)item).getYear());
//		} else {
			genericReorder(item.getClass().getSimpleName(), "project_id", projId, orderBy);
//		}
	}

	
	@SuppressWarnings("unchecked")
	public void deleteReferenceItem(int id) {
		ReferenceItem ref = getReferenceItem(id);
		  Probase p = ref.getProbase(); 
		  Integer wizardStep = p.getWizardStep();
		  int orderBy = ref.getOrderBy();
		  
		  // set probase as incomplete so it doesn't recalculate results table
		  p.setWizardStep(1);
		  
		  if (p.isProject()) { // unlink project items 
			  List<ProjectItem> items1= currentSession().createCriteria(ProjectItem.class).add(Restrictions.eq("linkedTo", ref)).list();
			  for (ProjectItem item : items1) { 
				  item.setLinkedTo(null); 
				  storeProjectItem(item); 
			  }
			  
			  List<BlockItem> items3= currentSession().createCriteria(BlockItem.class).add(Restrictions.eq("linkedTo", ref)).list();
			  for (BlockItem item : items3) { 
				  item.setLinkedTo(null);
				  storeBlockItem(item); 
			  }
		  } else { // unlink profile items 
			  storeProfile((Profile)p, false);
			  List<ProfileItem> items1=currentSession().createCriteria(ProfileItem.class).add(Restrictions.eq("linkedTo", ref)).list();
			  for (ProfileItem item : items1) { 
				  item.setLinkedTo(null);
				  storeProfileItem(item); 
			  } 
			  List<ProfileProductItem> items3= currentSession().createCriteria(ProfileProductItem.class).add(Restrictions.eq("linkedTo", ref)).list();
			  for (ProfileProductItem item : items3) { 
				  item.setLinkedTo(null);
				  storeProfileProductItem(item); 
			  }
		  }

		  //delete ref item
		  if (ref.getClass().isAssignableFrom(ReferenceIncome.class)) {
			  p.getRefIncomes().remove(ref);
		  } else if (ref.getClass().isAssignableFrom(ReferenceCost.class)) {
			  p.getRefCosts().remove(ref);
		  } else {
			  p.getRefLabours().remove(ref);
		  }
		  currentSession().delete(ref);
	  
		  // reorder remaining ref items 
		  String parentIdName = p.isProject() ? "Project_Id" : "Profile_Id";
		  genericReorder(ref.getClass().getSimpleName(), parentIdName, p.getProId(), orderBy); 
		  
		  // reset wizard step and calculate results if complete
		  p.setWizardStep(wizardStep);
		  if (wizardStep==null) {
			  if (p.isProject()) { storeProjectResult(p.getProId()); }
			  else { storeProfileResult(p.getProId()); }
		  }
	}
	 

	public void deleteAppConfig(AppConfig ac) {
		currentSession().delete(ac);
	}

	
	public void storeAppConfig(AppConfig ac) {
		if (ac.getConfigId() == null) {
			// get highest id from DB and add one
			Criteria criteria = currentSession()
					.createCriteria(AppConfig.class).setMaxResults(1)
					.addOrder(Order.desc("configId"));
			AppConfig existAc = (AppConfig) criteria.uniqueResult();
			int id = (existAc == null || existAc.getConfigId() < 0) ? 1
					: existAc.getConfigId() + 1;
			ac.setConfigId(id);
		}
		currentSession().saveOrUpdate(ac);
	}

	public AppConfig getAppConfig(int id) {
		return (AppConfig) currentSession()
				.createCriteria(AppConfig.class)
				.add(Restrictions.eqOrIsNull("id", id)).uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<Integer> donorsUsed(int projectId) {
		String sql = 
				"SELECT DISTINCT pid.donor_order_by FROM project_item_donation pid "+
				"JOIN project_item pi ON pi.proj_item_id=pid.item_id "+
				"WHERE pi.project_id=:projectId";
		String sql2 =
				"SELECT DISTINCT bid.donor_order_by FROM project_block_item_donation bid "+
				"JOIN project_block_item bi ON bi.prod_item_id=bid.block_item_id "+
				"JOIN project_block b ON b.block_id=bi.block_id "+
				"WHERE b.project_id=:projectId";
		String sql3 = 
				"SELECT DISTINCT donor_order_by FROM project_item "+
				"WHERE project_id=:projectId AND donor_order_by IS NOT NULL";
		SQLQuery query = currentSession().createSQLQuery(sql);
		query.setInteger("projectId", projectId);
		List<Integer> results = (List<Integer>)query.list();
		
		query = currentSession().createSQLQuery(sql2);
		query.setInteger("projectId", projectId);
		for (Integer i : (List<Integer>)query.list()) {
			if (!results.contains(i)) { results.add(i); }
		}
		
		query = currentSession().createSQLQuery(sql3);
		query.setInteger("projectId", projectId);
		for (Integer i : (List<Integer>)query.list()) {
			if (!results.contains(i)) { results.add(i); }
		}
		
		return results;
	}
	
	@SuppressWarnings("unchecked")
	public Map<Integer, Integer> appConfigUsage(String appConfigType,
			String property, boolean andProfile) {
		Map<Integer, Integer> usage = new HashMap<Integer, Integer>();

		ArrayList<Object[]> results = (ArrayList<Object[]>) currentSession()
				.createQuery(
						"select ac.configId, count(ac) from " + appConfigType
								+ " ac , Project p where p." + property
								+ "=ac.configId group by ac").list();
		for (int i = 0; i < results.size(); i++) {
			usage.put((Integer) results.get(i)[0],
					Integer.valueOf(results.get(i)[1].toString()));
		}

		if (andProfile) {
			results = (ArrayList<Object[]>) currentSession()
					.createQuery(
							"select ac.configId, count(ac) from "
									+ appConfigType
									+ " ac , Profile p where p." + property
									+ "=ac.configId group by ac").list();
			for (int i = 0; i < results.size(); i++) {
				Integer id = (Integer) results.get(i)[0];
				Integer count = Integer.valueOf(results.get(i)[1].toString());
				if (usage.containsKey(id))
					usage.put(id, ((Integer) usage.get(id)) + count);
				else
					usage.put(id, count);
			}
		}
		return usage;
	}

	@SuppressWarnings("unchecked")
	public List<User> getUsers() {
		return currentSession().createCriteria(User.class)
				.addOrder( Order.asc("description").ignoreCase() )
				.list();
	}

	public Setting getAppSetting() {
		return (Setting) currentSession()
				.createCriteria(Setting.class)
				.addOrder(Order.desc("settingId")).setMaxResults(1)
				.setFetchSize(1).uniqueResult();
	}

	public void storeAppSetting(Setting s) {
		currentSession().saveOrUpdate(s);
	}

//	@Cacheable("beneficiaries")
	@SuppressWarnings("unchecked")
	public List<Beneficiary> getBeneficiaries() {
		return currentSession()
				.createCriteria(Beneficiary.class)
				.addOrder(Order.asc("configId")).list();
	}

	@SuppressWarnings("unchecked")
	public List<FieldOffice> getFieldOffices() {
		return currentSession()
				.createCriteria(FieldOffice.class)
				.addOrder(Order.asc("configId")).list();
	}

	@SuppressWarnings("unchecked")
	public List<ProjectCategory> getProjectCategories() {
		return currentSession()
				.createCriteria(ProjectCategory.class)
				.addOrder(Order.asc("incomeGen"))
				.addOrder(Order.asc("configId"))
				.list();
	}

	@SuppressWarnings("unchecked")
	public List<EnviroCategory> getEnviroCategories() {
		return currentSession()
				.createCriteria(EnviroCategory.class)
				.addOrder(Order.asc("configId")).list();
	}

	@SuppressWarnings("unchecked")
	public List<Status> getStatuses() {
		return currentSession().createCriteria(Status.class)
				.addOrder(Order.asc("configId")).list();
	}

	@SuppressWarnings("unchecked")
	public List<AppConfig1> getAppConfig1s() {
		return currentSession()
				.createCriteria(AppConfig1.class)
				.addOrder(Order.asc("configId")).list();
	}

	@SuppressWarnings("unchecked")
	public List<AppConfig2> getAppConfig2s() {
		return currentSession()
				.createCriteria(AppConfig2.class)
				.addOrder(Order.asc("configId")).list();
	}

	public User getUserByUsername(String username) {
		return (User) currentSession()
				.createCriteria(User.class)
				.add(Restrictions.eq("username", username)).uniqueResult();
	}

	public User getUser(int id) {
		return (User) currentSession()
				.createCriteria(User.class).add(Restrictions.eq("userId", id))
				.uniqueResult();
	}

	public void storeUser(User u) {
		currentSession().saveOrUpdate(u);
	}

	public void deleteUser(User u) {
		Query deleteProfile = currentSession().createQuery(
				"delete from Profile where technician.userId= :userId");
		deleteProfile.setInteger("userId", u.getUserId());
		deleteProfile.executeUpdate();

		Query deleteProject = currentSession().createQuery(
				"delete from Project where technician.userId= :userId");
		deleteProject.setInteger("userId", u.getUserId());
		deleteProject.executeUpdate();

		currentSession().delete(u);
	}

	public void moveProfItem(ProfileItem item, boolean up) {
		genericMoveOrder(item.getClass().getSimpleName(), "Prof_Item_Id",
				item.getProfItemId(), "Profile_Id", item.getProfile()
						.getProfileId(), item.getOrderBy(), up);
	}

	public void moveProfileProduct(ProfileProductBase p, boolean up) {
		genericMoveOrder(p.getClass().getSimpleName(), "product_id",
				p.getProductId(), "profile_id", p.getProfile().getProfileId(),
				p.getOrderBy(), up);
	}

	public void moveProfProdItem(ProfileProductItem item, boolean up) {
		genericMoveOrder(item.getClass().getSimpleName(), "Prod_Item_Id",
				item.getProdItemId(), "Product_Id", item.getProfileProduct()
						.getProductId(), item.getOrderBy(), up);
	}
	
	public void moveProjectItem(ProjectItem item, boolean up) {
//		if (item.getClass().isAssignableFrom(ProjectItemContribution.class)) {
//			genericMoveOrderWithYear(item.getClass().getSimpleName(), "Proj_Item_Id", item.getProjItemId(), "Project_Id", 
//					item.getProject().getProjectId(), item.getOrderBy(), ((ProjectItemContribution)item).getYear(), up);
//		} else {
			genericMoveOrder(item.getClass().getSimpleName(), "Proj_Item_Id", item.getProjItemId(), "Project_Id", item.getProject().getProjectId(), item.getOrderBy(), up);
//		}
	}

	public void moveBlockItem(BlockItem item, boolean up) {
		genericMoveOrder(item.getClass().getSimpleName(), "Prod_Item_Id", item.getProdItemId(), "Block_Id", item.getBlock().getBlockId(), item.getOrderBy(), up);
	}
	
	@Transactional
	public void moveReferenceItem(ReferenceItem item, boolean up) {
		if (item.getProject() != null) {
			genericMoveOrder(item.getClass().getSimpleName(), "Ref_Item_Id",
					item.getRefItemId(), "Project_Id", item.getProbase()
							.getProId(), item.getOrderBy(), up);
		} else {
			genericMoveOrder(item.getClass().getSimpleName(), "Ref_Item_Id",
					item.getRefItemId(), "Profile_Id", item.getProbase()
							.getProId(), item.getOrderBy(), up);
		}
	}

	@Transactional
	private void genericMoveOrder(String className, String idField, int itemId,
			String parentIdName, int parentId, int orderBy, boolean dirUp) {
		genericMoveOrderWithYear(className, idField, itemId,
				parentIdName, parentId, orderBy, null, dirUp);
		
	}
	
	@Transactional
	private void genericMoveOrderWithYear(String className, String idField, int itemId,
			String parentIdName, int parentId, int orderBy, Integer year, boolean dirUp) {

		String dir1 = dirUp ? "+1" : "-1";
		String strQry1 = "UPDATE " + className + " SET order_By = (order_By"
				+ dir1 + ") WHERE " + idField + "=:id";
		Query query1 = currentSession().createQuery(strQry1);
		query1.setInteger("id", itemId);
		// int result =
		query1.executeUpdate();

		String dir2 = dirUp ? "-1" : "+1";
		String strQry2 = "UPDATE " + className + " SET order_By = (order_By"
				+ dir2 + ") WHERE order_By=:order_by AND " + parentIdName
				+ "=:pId AND " + idField + " <>:id";
		if (year!=null) {
			strQry2+=" AND year_begin=:year";
		}
		Query query2 = currentSession().createQuery(strQry2);
		int replaceWith = dirUp ? orderBy + 1 : orderBy - 1;
		query2.setInteger("order_by", replaceWith);
		query2.setInteger("pId", parentId);
		query2.setInteger("id", itemId);
		if (year!=null) {
			query2.setInteger("year", year.intValue());
		}
		query2.executeUpdate();
	}

	@Transactional
	private void genericReorder(String className, String parentIdName,
			int parentId, int orderBy) {
		genericReorderWithYear(className, parentIdName, parentId, orderBy, null);
	}
	
	@Transactional
	private void genericReorderWithYear(String className, String parentIdName,
			int parentId, int orderBy, Integer year) {
		//className = className.substring(0,className.indexOf("_$$_javassist"));
		String updateQuery = "UPDATE " + className
				+ " SET order_By = (order_By-1) WHERE order_By>:order_by AND "
				+ parentIdName + "=:pId";
		if (year!=null) {
			updateQuery+=" AND year_begin=:year";
		}
		Query reorder = currentSession().createQuery(
				updateQuery);
		reorder.setInteger("order_by", orderBy);
		reorder.setInteger("pId", parentId);
		if (year!=null) {
			reorder.setInteger("year", year.intValue());
		}
		reorder.executeUpdate();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public HomeData homeData() {
		HomeData hd = new HomeData();

		int[] stats = new int[8];
		
		stats[0] = ((Long) currentSession().createQuery("select count(*) from ProfileResult p where p.incomeGen is true").uniqueResult()).intValue();
		stats[1] = ((Long) currentSession().createQuery("select count(*) from Profile p where p.wizardStep is not null and p.incomeGen is true").uniqueResult()).intValue();
		stats[2] = ((Long) currentSession().createQuery("select count(*) from ProfileResult p where p.incomeGen is false").uniqueResult()).intValue();
		stats[3] = ((Long) currentSession().createQuery("select count(*) from Profile p where p.wizardStep is not null and p.incomeGen is false").uniqueResult()).intValue();
		
		stats[4] = ((Long) currentSession().createQuery("select count(*) from ProjectResult p where p.incomeGen is true").uniqueResult()).intValue();
		stats[5] = ((Long) currentSession().createQuery("select count(*) from Project p where p.wizardStep is not null and p.incomeGen is true").uniqueResult()).intValue();
		stats[6] = ((Long) currentSession().createQuery("select count(*) from ProjectResult p where p.incomeGen is false").uniqueResult()).intValue();
		stats[7] = ((Long) currentSession().createQuery("select count(*) from Project p where p.wizardStep is not null and p.incomeGen is false").uniqueResult()).intValue();

		hd.setDbStats(stats);

		List projs = currentSession().createQuery(
						"SELECT p.projectId, p.projectName, p.lastUpdate " +
						"FROM Project p WHERE p.wizardStep IS NOT NULL ORDER BY p.lastUpdate DESC")
				.setMaxResults(3)
				.list();
		hd.setProjs(projs);

		List profs = currentSession().createQuery(
						"SELECT p.profileId, p.profileName, p.lastUpdate " +
						"FROM Profile p WHERE p.wizardStep IS NOT NULL ORDER BY p.lastUpdate DESC")
				.setMaxResults(3)
				.list();
		hd.setProfs(profs);

		return hd;
	}
}
