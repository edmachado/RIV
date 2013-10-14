package riv.web.service;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import riv.objects.AttachedFile;
import riv.objects.FilterCriteria;
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
import riv.objects.profile.ProfileItem;
import riv.objects.profile.ProfileItemGeneral;
import riv.objects.profile.ProfileItemGeneralWithout;
import riv.objects.profile.ProfileItemGood;
import riv.objects.profile.ProfileItemLabour;
import riv.objects.profile.ProfileProduct;
import riv.objects.profile.ProfileProductIncome;
import riv.objects.profile.ProfileProductInput;
import riv.objects.profile.ProfileProductItem;
import riv.objects.profile.ProfileProductLabour;
import riv.objects.profile.ProfileResult;
import riv.objects.project.BlockBase;
import riv.objects.project.BlockIncome;
import riv.objects.project.BlockInput;
import riv.objects.project.BlockItem;
import riv.objects.project.BlockLabour;
import riv.objects.project.Project;
import riv.objects.project.ProjectItem;
import riv.objects.project.ProjectItemAsset;
import riv.objects.project.ProjectItemAssetWithout;
import riv.objects.project.ProjectItemContribution;
import riv.objects.project.ProjectItemGeneral;
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
import riv.objects.reference.ReferenceItem;

@Service("dataService")
public class DataService implements UserDetailsService {
	@Autowired
	private DataRepository repo;
	
	public void deleteAll(boolean project, boolean incomeGen) {
		repo.deleteAll(project, incomeGen);
	}
	
	public void deleteAllAppConfigs() {
		repo.deleteAllAppConfigs();
	}
	
	public void replaceProjectContribution(int projectId, List<ProjectItemContribution> items) {
		repo.replaceProjectContribution(projectId, items);
	}
	
	public void replaceProfileProduct(int productId, List<ProfileProductIncome> incs, List<ProfileProductInput> inps, List<ProfileProductLabour> labs) {
		repo.replaceProfileProduct(productId, incs, inps, labs);
	}
	
	public void replaceProjectGeneralNongen(int projectId, List<ProjectItemNongenMaterials> materials, List<ProjectItemNongenLabour> labours, List<ProjectItemNongenMaintenance> maints) {
		repo.replaceProjectGeneralNongen(projectId, materials, labours, maints);
	}
	
	public void replaceProfileGeneral(int profileId, List<ProfileItemGeneral> gens, List<ProfileItemGeneralWithout> gensWo) {
		repo.replaceProfileGeneral(profileId, gens, gensWo);
	}
	
	public void replaceBlock(int blockId, List<BlockIncome> incs, List<BlockInput> inps, List<BlockLabour> labs) {
		repo.replaceBlock(blockId, incs, inps, labs);
	}
	
	public void replaceProjectGeneral(int projectId, List<ProjectItemGeneral> gens, List<ProjectItemPersonnel> pers, List<ProjectItemGeneralWithout> gensWo, List<ProjectItemPersonnelWithout> persWo) {
		repo.replaceProjectGeneral(projectId, gens, pers, gensWo, persWo);
	}
	
	public void replaceProjectInvest(int projectId, List<ProjectItemAsset> assets, List<ProjectItemLabour> labours, List<ProjectItemService> services, List<ProjectItemAssetWithout> assetsWo, List<ProjectItemLabourWithout> laboursWo, List<ProjectItemServiceWithout> servicesWo) {
		repo.replaceProjectInvest(projectId, assets, labours, services, assetsWo, laboursWo, servicesWo);
	}
	
	public void replaceProfileInvest(int profileId, List<ProfileItemGood> goods, List<ProfileItemLabour> labours) {
		repo.replaceProfileInvest(profileId, goods, labours);
	}
	
	public Version getLatestVersion() {
		return repo.getLatestVersion();
	}
	public void storeVersion(Version v) {
		repo.storeVersion(v);
	}
	
	public boolean isIdsUsedByOtherClasses(Class<? extends AppConfig> clazz, Set<Integer> ids) {
		return repo.isIdsUsedByOtherClasses(clazz, ids);
	}

//	public void addOrgLogo(byte[] bytes, Setting setting) {
//		repo.addOrgLogo(bytes, setting);
//	}
	public List<AttachedFile> getAttachedFiles(int id, boolean isProject, boolean loadContentBytes) {
		return repo.getAttachedFiles(id, isProject, loadContentBytes);
	}
	public void copyAttachedFileContent(int id, OutputStream out, boolean isProject) {
		repo.streamAttachedFileContent(id, out, isProject);
	}
	public boolean profileFileExistsWithFilename(int profileId, boolean isProject, String filename) {
		return repo.attachedFileExistsWithFilename(profileId, isProject, filename);
	}
	public void addAttachedFile(byte[] filebytes, AttachedFile pf) {
		repo.addAttachedFile(filebytes, pf);
	}
	public void deleteAttachedFile(int id, boolean isProject) {
		repo.deleteAttachedFile(id, isProject);
	}
	public void copyAttachedFiles(Probase source, Probase target) {
		repo.copyAttachedFiles(source, target);
	}
	
	public List<ProfileResult> getProfileResults(FilterCriteria fc) {
		return repo.getProfileResults(fc);
	}
	public List<Profile> getProfileUnfinished(boolean incomeGen) {
		return repo.getProfileUnfinished(incomeGen);
	}
	public Profile getProfileByUniqueId(byte[] uid) {
		return repo.getProfileByUniqueId(uid);
	}
	public Project getProjectByUniqueId(byte[] uid) {
		return repo.getProjectByUniqueId(uid);
	}
	public Profile getProfile(int id, int step) {
		return repo.getProfile(id, step);
	}
	public void storeProfile(Profile p, boolean calculateResult) {
		 repo.storeProfile(p, calculateResult);
	}
	public void storeProfileResult(Integer id) {
		repo.storeProfileResult(id);
	}
	public void deleteProfile(Profile p) {
		repo.deleteProfile(p);
	}
	public ProfileResult getProfileResult(int id) {
		return repo.getProfileResult(id);
	}
	
	public ProfileItem getProfileItem(int id) {
		return repo.getProfileItem(id);
	}
	public void storeProfileItem(ProfileItem pi) {
		repo.storeProfileItem(pi);
	}
	public void deleteProfileItem(ProfileItem pi) {
		repo.deleteProfileItem(pi);
	}
	
	public ProfileProduct getProfileProduct(int id) {
		return repo.getProfileProduct(id, null);
	}
	public ProfileProduct getProfileProduct(int id, String collectionToInizialize) {
		return repo.getProfileProduct(id, collectionToInizialize);
	}
	public void storeProfileProduct(ProfileProduct pp) {
		repo.storeProfileProduct(pp);
	}
	public void deleteProfileProduct(ProfileProduct pp) {
		repo.deleteProfileProduct(pp);
	}
		
	
	public ProfileProductItem getProfileProductItem(int id) {
		return repo.getProfileProductItem(id);
	}
	public void storeProfileProductItem(ProfileProductItem pi) {
		repo.storeProfileProductItem(pi);
	}
	public void deleteProfileProductItem(ProfileProductItem pi) {
		repo.deleteProfileProductItem(pi);
	}
	
	public List<ProjectResult> getProjectResults(FilterCriteria filter) {
		return repo.getProjectResults(filter);
	}
	public List<Project> getProjectUnfinished(boolean incomeGen) {
		return repo.getProjectUnfinished(incomeGen);
	}
	
	public ProjectResult getProjectResult(int id) {
		return repo.getProjectResult(id);
	}
	public Project getProject(int id, int step) {
		return repo.getProject(id, step);
	}
	public void storeProject(Project project, boolean storeResult) {
		repo.storeProject(project, storeResult);
	}
	public void deleteProject(Project p) {
		repo.deleteProject(p);
	}
	public void storeProjectResult(int id) {
		repo.storeProjectResult(id);
	}
	public void updatePatternLength(int projectId, int duration, int oldDuration) {
		repo.updatePatternLength(projectId, duration, oldDuration);
	}
	public void updateBlocksWithWithout(int projectId, boolean withWithout) {
		repo.updateBlocksWithWithout(projectId, withWithout);
	}
	public void updateProductsWithWithout(int profileId, boolean withWithout) {
		repo.updateProductsWithWithout(profileId, withWithout);
	}
	
	public ProjectItem getProjectItem(int id) {
		return repo.getProjectItem(id);
	}
	public void storeProjectItem(ProjectItem pi) {
		repo.storeProjectItem(pi);
	}
	public void deleteProjectItem(ProjectItem pi) {
		repo.deleteProjectItem(pi);
	}
	
	public BlockBase getBlock(int id) {
		return repo.getBlock(id, null);
	}
	public BlockBase getBlock(int id, String collectionToInitialize) {
		return repo.getBlock(id, collectionToInitialize);
	}
	public void storeBlock(BlockBase b) {
		repo.storeBlock(b);
	}
	public void deleteBlock(BlockBase b) {
		repo.deleteBlock(b);
	}
	public void moveBlock(BlockBase b, boolean up) {
		repo.movBlock(b, up);
	}
	
	public BlockItem getBlockItem(int id) {
		return repo.getBlockItem(id);
	}
	public void storeBlockItem(BlockItem bi) {
		repo.storeBlockItem(bi);
	}
	public void deleteBlockItem(BlockItem bi) {
		repo.deleteBlockItem(bi);
	}
	
	public void checkProfilesOnUpgrade() {
		repo.checkProfilesOnUpgrade();
	}
	public void checkProjectsOnUpgrade() {
		repo.checkProjectsOnUpgrade();
	}
	
	public void recalculateCompletedProjects() {
		repo.recalculateCompletedProjects();
	}
	public void recalculateCompletedProfiles() {
		repo.recalculateCompletedProfiles();
	}
	
	public ReferenceItem getReferenceItem(int id) {
		return repo.getReferenceItem(id);
	}
	public void storeReferenceItem(ReferenceItem ri) {
		repo.storeReferenceItem(ri);
	}
	public void updateReferenceLinks(ReferenceItem ref) {
		repo.updateReferenceLinks(ref);
	}
	public void deleteReferenceItem(ReferenceItem ri) {
		repo.deleteReferenceItem(ri);
	}
	
	public void deleteAppConfig(AppConfig ac) {
		repo.deleteAppConfig(ac);
	}
	public void storeAppConfig(AppConfig ac) {
		repo.storeAppConfig(ac);
	}
	public AppConfig getAppConfig(int id) {
		return repo.getAppConfig(id);
	}
	
	public Map<Integer, Integer> appConfigUsage(String appConfigType, String property, boolean andProfile) {
		return repo.appConfigUsage(appConfigType, property, andProfile);
	}
	
	public List<User> getUsers() {
		return repo.getUsers();
	}
	public User getUser(int id) {
		return repo.getUser(id);
	}
	public User getUserByUsername(String username) {
		return repo.getUserByUsername(username);
	}
	public void storeUser(User u) {
		repo.storeUser(u);
	}
	public void deleteUser(User u) {
		repo.deleteUser(u);
	}
	
	public Setting getAppSetting() {
		return repo.getAppSetting();
	}
	
	public void storeAppSetting(Setting s) {
		repo.storeAppSetting(s);
	}
	
	public List<Beneficiary> getBeneficiaries() {
		return repo.getBeneficiaries();
	}
	
	public List<FieldOffice> getFieldOffices() {
		return repo.getFieldOffices();
	}
	
	public List<ProjectCategory> getProjectCategories() {
		return repo.getProjectCategories();
	}
	
	public List<EnviroCategory> getEnviroCategories() {
		return repo.getEnviroCategories();
	}
	
	public List<Status> getStatuses() {
		return repo.getStatuses();
	}
	
	public List<AppConfig1> getAppConfig1s() {
		return repo.getAppConfig1s();
	}

	public List<AppConfig2> getAppConfig2s() {
		return repo.getAppConfig2s();
	}

	@Override
	public UserDetails loadUserByUsername(String username) {
		UserDetails ud = repo.getUserByUsername(username);
		return ud;
	}
	
	public void moveProfItem(ProfileItem item, boolean up) {
		repo.moveProfItem(item, up);
	}
	public void moveProfileProduct(ProfileProduct p, boolean up) {
		repo.moveProfileProduct(p, up);
	}
	public void moveProfProdItem(ProfileProductItem item, boolean up) {
		repo.moveProfProdItem(item, up);
	}
	public void moveReferenceItem(ReferenceItem item, boolean up) {
		repo.moveReferenceItem(item, up);
	}
	public void moveProjectItem(ProjectItem item, boolean up) {
		repo.moveProjectItem(item, up);
	}
	public void moveBlockItem(BlockItem item, boolean up) {
		repo.moveBlockItem(item, up);
	}
	
	public HomeData homeData() {
		return repo.homeData();
	}
}