package org.closure.app.CommunityModule.services;

import java.util.ArrayList;
import java.util.List;

import org.closure.app.CommunityModule.dto.CommunityResponse;
import org.closure.app.CommunityModule.exceptions.CommunityErrorException;
import org.closure.app.CommunityModule.mapper.CommunityMapper;
import org.closure.app.CommunityModule.models.CommunityModel;
import org.closure.app.CommunityModule.repositories.CommunityRepo;
import org.closure.app.UserModule.exceptions.UserErrorException;
import org.closure.app.UserModule.models.UserModel;
import org.closure.app.UserModule.repositories.UserRepo;
import org.closure.app.entities.CommunityEntity;
import org.closure.app.entities.UserEntity;
import org.closure.app.postModule.dto.PostResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CommunityService {

    @Autowired
    CommunityRepo communityRepo;

    @Autowired
    UserRepo userRepo;

    public CommunityResponse findByUsers(UserEntity userEntity)
    {
        return CommunityMapper.INSTANCE.communityToResponse(
            communityRepo.findByUsers(userEntity).orElseThrow(
                ()-> new CommunityErrorException("can not find community for this user")
            )
        );
    } 

    public CommunityResponse findById(Long id)
    {
        
            return CommunityMapper.INSTANCE.communityToResponse(
                communityRepo.findById(id).orElseThrow(
                    ()-> new CommunityErrorException("no community for this id")
                )
            );
    }

    public CommunityResponse findByName(String name)
    {
            return CommunityMapper.INSTANCE.communityToResponse(
                communityRepo.findByName(name).orElseThrow(
                    ()-> new CommunityErrorException("no community with this name")
                )
            );
    }

    public String addUsreToCommunity(Long userId, Long communityId)
    {
        return addUser(userId, communityId) ? 
            "user with id "+userId+" added  to community" : 
            "for some reasons you can't join this community";
    }

    private boolean addUser(Long userId, Long communityId)
    {
        CommunityEntity communityEntity = communityRepo
            .findById(communityId)
            .orElseThrow(() -> new CommunityErrorException(
                    "community with id " + communityId + " not found")
            );

        return userRepo.findById(userId).map(
            user -> {
                user.setCommuninty(communityEntity);
                return userRepo.save(user);
        }).get().getCommuninty().getId().equals(communityId);
    }

    public CommunityModel addCommunity(CommunityModel communityModel)
    {
        if(communityRepo.findByName(communityModel.getName()).isEmpty())
        {
           
            return CommunityMapper.INSTANCE.communityToModel(
                communityRepo.save(
                    CommunityMapper.INSTANCE.modelToCommunity(communityModel)
                )
            );

        }else throw new CommunityErrorException("this community is already exist");
    }

    public String deleteCommunity(Long id)
    {
        return delete(id) ? "community deleted" :"for some reason you can't delete this community";
    }

    private boolean delete(Long id)
    {
        CommunityEntity community = communityRepo.findById(id).orElseThrow(
            () -> new UserErrorException("error in id"));
        communityRepo.delete(community);
        return communityRepo.findById(id).isEmpty();
    }

    public List<CommunityResponse> search(String value)
    {
        return communityRepo.findByNameLike(value).stream().map(
            CommunityMapper.INSTANCE::communityToResponse
        ).toList();
    }

    public CommunityResponse edit(CommunityModel communityModel)
    {
           
        return CommunityMapper.INSTANCE.communityToResponse(
            communityRepo.save(
                communityRepo.findById(communityModel.getId()).orElseThrow(
                    ()-> new CommunityErrorException("no community with this id")
                )
            )
        );
    }

    public List<CommunityModel> getAll()
    {
        return communityRepo.findAll().stream().map(CommunityMapper.INSTANCE::communityToModel).toList();
    }

    public List<UserModel> getUsers(Long communityId)
    {
        List<UserModel> models = new ArrayList<>();
        CommunityEntity communityEntity = communityRepo.findById(communityId)
            .orElseThrow(()->new CommunityErrorException("no community with this di"));
        communityEntity.getUsers().forEach((e)->{
            UserModel model = new UserModel()
                .withAge(e.getAge())
                .withCommunity_name(e.getCommunity_name())
                .withEmail(e.getEmail())
                .withId(e.getId())
                .withImg(e.getImg())
                .withName(e.getName())
                .withStart_year(e.getStart_year())
                .withStudy_year(e.getStudy_year());
                models.add(model);
        });
        return models;
        //TODO : ADD MAPPER FEATURE FOR USER MODEL
    }

    public List<PostResponse> getPosts(Long communityID)
    {
        CommunityEntity cEntity = communityRepo.findById(communityID).orElseThrow(
            () -> new UserErrorException("no community with this id"));
        List<PostResponse> postResponses = new ArrayList<>();
        cEntity.getPosts().forEach(
            (p) -> {
                PostResponse postResponse = new PostResponse()
                    .withAttach(p.getAttach())
                    .withCommunityID(p.getPcommuninty().getId())
                    .withPostID(p.getId())
                    .withTitle(p.getTitle())
                    .withUserID(p.getUEntity().getId())
                    .withValue(p.getValue());
                postResponses.add(postResponse);
            });
        return postResponses;
            //TODO : ADD MAPPER FEATURE FOR POST MODEL
    }
    
}

