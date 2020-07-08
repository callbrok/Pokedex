package com.xeroxparc.pokedex.ui.egggroups.lists.adapters;

import android.util.Log;
import android.widget.Filter;

import com.xeroxparc.pokedex.data.model.pokemon.species.PokemonSpecies;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EggGroupSpeciesListFilter extends Filter {
    private static final String TAG = "EggGroupSpeciesFilter";
    private List<PokemonSpecies> fullList;
    private FilterMode filterMode;
    private PostFilteringCallBack<List<PokemonSpecies>> callBack;
    private Map<String, PokemonSpecies> detailedSpeciesMap;

    public EggGroupSpeciesListFilter(List<PokemonSpecies> originalList, Map<String, PokemonSpecies> detailedSpeciesMap,
                                     FilterMode mode, PostFilteringCallBack<List<PokemonSpecies>> postFilteringCallBack) {
        fullList = originalList;
        filterMode = mode;
        callBack = postFilteringCallBack;
        this.detailedSpeciesMap = detailedSpeciesMap;
    }

    @Override
    protected FilterResults performFiltering(CharSequence filterKeyword) {
        List<PokemonSpecies> filteredList = new ArrayList<>();
        if (filterMode == null) {
            if (filterKeyword != null && filterKeyword.length() > 0) {
                String formattedKeyword = String.valueOf(filterKeyword).trim();
                filteredList.addAll(fullList.stream().
                        filter(specie -> specie
                                .getName()
                                .trim()
                                .contains(formattedKeyword))
                        .collect(Collectors.toList()));
            } else {
                filteredList.addAll(fullList);
            }
        } else {
            switch (filterMode) {
                case MODE_ONLY_UNIQUE_EGG_GROUP:
                    Log.e(TAG, "performFiltering: ONLY UNIQUE");
                    filteredList.addAll(fullList.stream().
                            filter(specie -> detailedSpeciesMap.get(specie.getName()) != null &&
                                    detailedSpeciesMap.get(specie.getName()).getEggGroupsList().size() == 1).
                            collect(Collectors.toList()));
                    break;
                case MODE_UNIQUE_AND_OTHER_EGG_GROUPS:
                    Log.e(TAG, "performFiltering: COMMON");
                    filteredList.addAll(fullList.stream().
                            filter(specie -> detailedSpeciesMap.get(specie.getName()) != null &&
                                    detailedSpeciesMap.get(specie.getName()).getEggGroupsList().size() > 1).
                            collect(Collectors.toList()));
                    break;
                case MODE_ALL:
                    Log.e(TAG, "performFiltering: ALL");
                    filteredList.addAll(fullList);
                    break;
            }
        }

        FilterResults results = new FilterResults();
        results.values = filteredList;
        return results;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
        if (filterResults.values instanceof List) {
            List<PokemonSpecies> filteredSpecies = (List<PokemonSpecies>) filterResults.values;
            callBack.postFiltering(filteredSpecies);
        } else {
            Log.e(TAG, "publishResults: Invalid filtered data values type!");
        }
    }

    public enum FilterMode {
        MODE_ONLY_UNIQUE_EGG_GROUP, MODE_UNIQUE_AND_OTHER_EGG_GROUPS, MODE_ALL
    }
}
