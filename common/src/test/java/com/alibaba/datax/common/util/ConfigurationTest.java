package com.alibaba.datax.common.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import com.alibaba.fastjson.JSON;

public class ConfigurationTest {

	@Test
	public void test_get() {
		Configuration configuration = Configuration
				.from("{\"a\":[{\"b\":[{\"c\":[\"bazhen\"]}]}]}");

		String path = "";
		Assert.assertTrue(JSON.toJSONString(configuration.get(path)).equals(
				"{\"a\":[{\"b\":[{\"c\":[\"bazhen\"]}]}]}"));

		path = "a[0].b[0].c[0]";
		Assert.assertTrue(JSON.toJSONString(configuration.get(path)).equals(
				"\"bazhen\""));

		configuration = Configuration.from("{\"a\": [[[0]]]}");
		path = "a[0][0][0]";
		System.out.println(JSON.toJSONString(configuration.get(path)));
		Assert.assertTrue(JSON.toJSONString(configuration.get(path))
				.equals("0"));

		path = "a[0]";
		System.out.println(JSON.toJSONString(configuration.get(path)));
		Assert.assertTrue(JSON.toJSONString(configuration.get(path)).equals(
				"[[0]]"));

		path = "c[0]";
		System.out.println(JSON.toJSONString(configuration.get(path)));
		Assert.assertTrue(JSON.toJSONString(configuration.get(path)).equals(
				"null"));

		configuration = Configuration.from("[1,2]");
		System.out.println(configuration.get("[0]"));
		Assert.assertTrue(configuration.getString("[0]").equals("1"));
		Assert.assertTrue(configuration.getString("[1]").equals("2"));

	}

	@Test
	public void test_buildObject() {

		// 非法参数
		try {
			Configuration.from("{}").buildObject(null, "bazhen");
			Assert.assertTrue(false);
		} catch (Exception e) {
			Assert.assertTrue(true);
		}

		// 测试单元素
		Assert.assertTrue(Configuration.from("{}")
				.buildObject(new ArrayList<String>(), "bazhen")
				.equals("bazhen"));
		Assert.assertTrue(Configuration.from("{}").buildObject(
				new ArrayList<String>(), new HashMap<String, Object>()) instanceof Map);
		Assert.assertTrue(Configuration.from("{}").buildObject(
				new ArrayList<String>(), null) == null);

		// 测试多级元素
		String path = null;
		String json = null;

		path = "";
		json = JSON.toJSONString(Configuration.from("{}").buildObject(
				Arrays.asList(StringUtils.split(path, ".")), "bazhen"));
		System.out.println(json);
		Assert.assertTrue("\"bazhen\"".equals(json));

		path = "a";
		json = JSON.toJSONString(Configuration.from("{}").buildObject(
				Arrays.asList(StringUtils.split(path, ".")), "bazhen"));
		System.out.println(json);
		Assert.assertTrue("{\"a\":\"bazhen\"}".equals(json));

		path = "a";
		json = JSON.toJSONString(Configuration.from("{}").buildObject(
				Arrays.asList(StringUtils.split(path, ".")),
				new HashMap<String, Object>()));
		System.out.println(json);
		Assert.assertTrue("{\"a\":{}}".equals(json));

		path = "a";
		json = JSON.toJSONString(Configuration.from("{}").buildObject(
				Arrays.asList(StringUtils.split(path, ".")),
				new ArrayList<String>()));
		System.out.println(json);
		Assert.assertTrue("{\"a\":[]}".equals(json));

		path = "a";
		json = JSON.toJSONString(Configuration.from("{}").buildObject(
				Arrays.asList(StringUtils.split(path, ".")), 1L));
		System.out.println(json);
		Assert.assertTrue("{\"a\":1}".equals(json));

		path = "a";
		json = JSON.toJSONString(Configuration.from("{}").buildObject(
				Arrays.asList(StringUtils.split(path, ".")), 1.1));
		System.out.println(json);
		Assert.assertTrue("{\"a\":1.1}".equals(json));

		path = "[0]";
		json = JSON.toJSONString(Configuration.from("{}").buildObject(
				Arrays.asList(StringUtils.split(path, ".")), "bazhen"));
		System.out.println(json);
		Assert.assertTrue("[\"bazhen\"]".equals(json));

		path = "[1]";
		json = JSON.toJSONString(Configuration.from("{}").buildObject(
				Arrays.asList(StringUtils.split(path, ".")), "bazhen"));
		System.out.println(json);
		Assert.assertTrue("[null,\"bazhen\"]".equals(json));

		path = "a.b.c.d.e.f";
		json = JSON.toJSONString(Configuration.from("{}").buildObject(
				Arrays.asList(StringUtils.split(path, ".")), "bazhen"));
		System.out.println(json);
		Assert.assertTrue("{\"a\":{\"b\":{\"c\":{\"d\":{\"e\":{\"f\":\"bazhen\"}}}}}}"
				.equals(json));

		path = "[1].[1]";
		json = JSON.toJSONString(Configuration.from("{}").buildObject(
				Arrays.asList(StringUtils.split(path, ".")), "bazhen"));
		System.out.println(json);
		Assert.assertTrue("[null,[null,\"bazhen\"]]".equals(json));

		path = "a.[10].b.[0].c.[1]";
		json = JSON.toJSONString(Configuration.from("{}").buildObject(
				Arrays.asList(StringUtils.split(path, ".")), "bazhen"));
		System.out.println(json);
		Assert.assertTrue("{\"a\":[null,null,null,null,null,null,null,null,null,null,{\"b\":[{\"c\":[null,\"bazhen\"]}]}]}"
				.equals(json));
	}

	@Test
	public void test_setObjectRecursive() {
		// 当current完全为空，类似新插入对象

		String path = "";
		Object root = null;

		root = Configuration.from("{}").setObjectRecursive(null,
				Arrays.asList(StringUtils.split(path, ".")), 0, "bazhen");
		System.out.println(root);
		Assert.assertTrue(JSON.toJSONString(root).equals("\"bazhen\""));

		root = JSON.toJSONString(Configuration.from("{}").setObjectRecursive(
				null, Arrays.asList(StringUtils.split(path, ".")), 0,
				new ArrayList<String>()));
		System.out.println(root);
		Assert.assertTrue(root.equals("[]"));

		root = JSON.toJSONString(Configuration.from("{}").setObjectRecursive(
				null, Arrays.asList(StringUtils.split(path, ".")), 0,
				new HashMap<String, Object>()));
		System.out.println(root);
		Assert.assertTrue(root.equals("{}"));

		root = JSON.toJSONString(Configuration.from("{}").setObjectRecursive(
				null, Arrays.asList(StringUtils.split(path, ".")), 0, 0L));
		System.out.println(root);
		Assert.assertTrue(root.equals("0"));

		// 当current当前为空，但是path存在路径，类似新插入对象
		path = "a";
		root = JSON
				.toJSONString(Configuration.from("{}").setObjectRecursive(null,
						Arrays.asList(StringUtils.split(path, ".")), 0,
						"bazhen"));
		System.out.println(root);
		Assert.assertTrue("{\"a\":\"bazhen\"}".equals(root));

		path = "a.b";
		root = JSON
				.toJSONString(Configuration.from("{}").setObjectRecursive(null,
						Arrays.asList(StringUtils.split(path, ".")), 0,
						"bazhen"));
		System.out.println(root);
		Assert.assertTrue("{\"a\":{\"b\":\"bazhen\"}}".equals(root));

		path = "a.b.c.d.e.f.g.h.i.j.k.l.m.n.o.p.q.r.s.t.u.v.w.x.y.z";
		root = JSON
				.toJSONString(Configuration.from("{}").setObjectRecursive(null,
						Arrays.asList(StringUtils.split(path, ".")), 0,
						"bazhen"));
		System.out.println(root);
		Assert.assertTrue("{\"a\":{\"b\":{\"c\":{\"d\":{\"e\":{\"f\":{\"g\":{\"h\":{\"i\":{\"j\":{\"k\":{\"l\":{\"m\":{\"n\":{\"o\":{\"p\":{\"q\":{\"r\":{\"s\":{\"t\":{\"u\":{\"v\":{\"w\":{\"x\":{\"y\":{\"z\":\"bazhen\"}}}}}}}}}}}}}}}}}}}}}}}}}}"
				.equals(root));

		path = "1.1";
		root = JSON
				.toJSONString(Configuration.from("{}").setObjectRecursive(null,
						Arrays.asList(StringUtils.split(path, ".")), 0,
						"bazhen"));
		System.out.println(root);
		Assert.assertTrue("{\"1\":{\"1\":\"bazhen\"}}".equals(root));

		path = "-.-";
		root = JSON
				.toJSONString(Configuration.from("{}").setObjectRecursive(null,
						Arrays.asList(StringUtils.split(path, ".")), 0,
						"bazhen"));
		System.out.println(root);
		Assert.assertTrue("{\"-\":{\"-\":\"bazhen\"}}".equals(root));

		path = "[0]";
		root = JSON
				.toJSONString(Configuration.from("{}").setObjectRecursive(null,
						Arrays.asList(StringUtils.split(path, ".")), 0,
						"bazhen"));
		System.out.println(root);
		Assert.assertTrue(root.equals("[\"bazhen\"]"));

		path = "[0].[0]";
		root = JSON
				.toJSONString(Configuration.from("{}").setObjectRecursive(null,
						Arrays.asList(StringUtils.split(path, ".")), 0,
						"bazhen"));
		System.out.println(root);
		Assert.assertTrue(root.equals("[[\"bazhen\"]]"));

		path = "[0].[0].[0].[0].[0].[0].[0].[0].[0]";
		root = JSON
				.toJSONString(Configuration.from("{}").setObjectRecursive(null,
						Arrays.asList(StringUtils.split(path, ".")), 0,
						"bazhen"));
		System.out.println(root);
		Assert.assertTrue(root.equals("[[[[[[[[[\"bazhen\"]]]]]]]]]"));

		path = "[0].[1].[2].[3].[4].[5].[6].[7].[8]";
		root = JSON
				.toJSONString(Configuration.from("{}").setObjectRecursive(null,
						Arrays.asList(StringUtils.split(path, ".")), 0,
						"bazhen"));
		System.out.println(root);
		Assert.assertTrue(root
				.equals("[[null,[null,null,[null,null,null,[null,null,null,null,[null,null,null,null,null,[null,null,null,null,null,null,[null,null,null,null,null,null,null,[null,null,null,null,null,null,null,null,\"bazhen\"]]]]]]]]]"));

		path = "a.[0].b.[0].c.[0]";
		root = JSON
				.toJSONString(Configuration.from("{}").setObjectRecursive(null,
						Arrays.asList(StringUtils.split(path, ".")), 0,
						"bazhen"));
		System.out.println(root);
		Assert.assertTrue(root
				.equals("{\"a\":[{\"b\":[{\"c\":[\"bazhen\"]}]}]}"));

		// 初始化为list，测试插入对象

		root = JSON.parse("[]");
		path = "a";
		root = JSON
				.toJSONString(Configuration.from("{}").setObjectRecursive(root,
						Arrays.asList(StringUtils.split(path, ".")), 0,
						"bazhen"));
		System.out.println(root);
		Assert.assertTrue("{\"a\":\"bazhen\"}".equals(root));

		root = JSON.parse("[]");
		path = "a.b";
		root = JSON
				.toJSONString(Configuration.from("{}").setObjectRecursive(root,
						Arrays.asList(StringUtils.split(path, ".")), 0,
						"bazhen"));
		System.out.println(root);
		Assert.assertTrue("{\"a\":{\"b\":\"bazhen\"}}".equals(root));

		root = JSON.parse("[]");
		path = "[0]";
		root = JSON
				.toJSONString(Configuration.from("{}").setObjectRecursive(root,
						Arrays.asList(StringUtils.split(path, ".")), 0,
						"bazhen"));
		System.out.println(root);
		Assert.assertTrue(root.equals("[\"bazhen\"]"));

		root = JSON.parse("[]");
		path = "[0].[0]";
		root = JSON
				.toJSONString(Configuration.from("{}").setObjectRecursive(root,
						Arrays.asList(StringUtils.split(path, ".")), 0,
						"bazhen"));
		System.out.println(root);
		Assert.assertTrue(root.equals("[[\"bazhen\"]]"));

		// 初始化为map，测试插入对象
		root = JSON.parse("{}");
		path = "a";
		root = JSON
				.toJSONString(Configuration.from("{}").setObjectRecursive(root,
						Arrays.asList(StringUtils.split(path, ".")), 0,
						"bazhen"));
		System.out.println(root);
		Assert.assertTrue("{\"a\":\"bazhen\"}".equals(root));

		root = JSON.parse("{}");
		path = "a.b";
		root = JSON
				.toJSONString(Configuration.from("{}").setObjectRecursive(root,
						Arrays.asList(StringUtils.split(path, ".")), 0,
						"bazhen"));
		System.out.println(root);
		Assert.assertTrue("{\"a\":{\"b\":\"bazhen\"}}".equals(root));

		root = JSON.parse("{}");
		path = "[0]";
		root = JSON
				.toJSONString(Configuration.from("{}").setObjectRecursive(root,
						Arrays.asList(StringUtils.split(path, ".")), 0,
						"bazhen"));
		System.out.println(root);
		Assert.assertTrue(root.equals("[\"bazhen\"]"));

		root = JSON.parse("{}");
		path = "[0].[0]";
		root = JSON
				.toJSONString(Configuration.from("{}").setObjectRecursive(root,
						Arrays.asList(StringUtils.split(path, ".")), 0,
						"bazhen"));
		System.out.println(root);
		Assert.assertTrue(root.equals("[[\"bazhen\"]]"));

		root = JSON.parse("{\"a\": \"a\", \"b\":\"b\"}");
		path = "a.[0]";
		root = Configuration.from("{}").setObjectRecursive(root,
				Arrays.asList(StringUtils.split(path, ".")), 0, "bazhen");
		System.out.println(root);
		System.out.println(JSON.toJSONString(root).equals(
				"{\"a\":[\"bazhen\"],\"b\":\"b\"}"));

		root = JSON
				.parse("{\"a\":{\"b\":{\"c\":[0],\"B\": \"B\"},\"A\": \"A\"}}");
		path = "a.b.c.[0]";
		root = Configuration.from("{}").setObjectRecursive(root,
				Arrays.asList(StringUtils.split(path, ".")), 0, "bazhen");
		System.out.println(root);
		Assert.assertTrue(JSON.toJSONString(root).equals(
				"{\"a\":{\"A\":\"A\",\"b\":{\"B\":\"B\",\"c\":[\"bazhen\"]}}}"));
	}

	@Test
	public void test_setConfiguration() {
		Configuration configuration = Configuration.from("{}");
		configuration.set("b", Configuration.from("{}"));
		System.out.println(configuration.toJSON());
		Assert.assertTrue(configuration.toJSON().equals("{\"b\":{}}"));

		configuration = Configuration.newDefault();
		List<Configuration> list = new ArrayList<Configuration>();
		for (int i = 0; i < 3; i++) {
			list.add(Configuration.newDefault());
		}
		configuration.set("a", list);
		System.out.println(configuration.toJSON());
		Assert.assertTrue("{\"a\":[{},{},{}]}".equals(configuration.toJSON()));

		Map<String, Configuration> map = new HashMap<String, Configuration>();
		map.put("a", Configuration.from("{\"a\": 1}"));
		configuration.set("a", map);
		System.out.println(configuration.toJSON());
		Assert.assertTrue("{\"a\":{\"a\":{\"a\":1}}}".equals(configuration
				.toJSON()));
	}

	@Test
	public void test_set() {
		Configuration configuration = Configuration
				.from("{\"a\":{\"b\":{\"c\":[0],\"B\": \"B\"},\"A\": \"A\"}}");
		configuration.set("a.b.c[0]", 3.1415);
		Assert.assertTrue(configuration.toJSON().equals(
				"{\"a\":{\"A\":\"A\",\"b\":{\"B\":\"B\",\"c\":[3.1415]}}}"));

		configuration.set("a.b.c[1]", 3.1415);
		System.out.println(configuration.toJSON());
		Assert.assertTrue(configuration
				.toJSON()
				.equals("{\"a\":{\"A\":\"A\",\"b\":{\"B\":\"B\",\"c\":[3.1415,3.1415]}}}"));
		configuration.set("a.b.c[0]", null);
		System.out.println(configuration.toJSON());
		Assert.assertTrue(configuration
				.toJSON()
				.equals("{\"a\":{\"A\":\"A\",\"b\":{\"B\":\"B\",\"c\":[null,3.1415]}}}"));

		configuration.set("[0]", 3.14);
		System.out.println(configuration.toJSON());
		Assert.assertTrue(configuration.toJSON().equals("[3.14]"));

		configuration.set("[1]", 3.14);
		System.out.println(configuration.toJSON());
		Assert.assertTrue(configuration.toJSON().equals("[3.14,3.14]"));

		configuration.set("", new HashMap<String, Object>());
		System.out.println(configuration.toJSON());
		Assert.assertTrue(configuration.toJSON().equals("{}"));

		configuration = Configuration.newDefault();
		configuration.set("a[0].b", 1);
		configuration.set("a[0].b", 1);
		System.out.println(configuration.toJSON());
		Assert.assertTrue("{\"a\":[{\"b\":1}]}".equals(configuration.toJSON()));

		try {
			configuration.set(null, 3.14);
			Assert.assertFalse(true);
		} catch (Exception e) {
			Assert.assertTrue(true);
		}

		try {
			configuration.set("", 3.14);
			Assert.assertFalse(true);
		} catch (Exception e) {
			Assert.assertTrue(true);
		}
	}

	@Test
	public void test_getKeys() {
		Set<String> sets = new HashSet<String>();

		sets.clear();
		Configuration configuration = Configuration.from("{}");
		System.out.println(JSON.toJSONString(configuration.getKeys()));
		Assert.assertTrue(configuration.getKeys().isEmpty());

		sets.clear();
		configuration = Configuration.from("[]");
		System.out.println(JSON.toJSONString(configuration.getKeys()));
		Assert.assertTrue(configuration.getKeys().isEmpty());

		sets.clear();
		configuration = Configuration.from("[0]");
		System.out.println(JSON.toJSONString(configuration.getKeys()));
		Assert.assertTrue(configuration.getKeys().contains("[0]"));

		sets.clear();
		configuration = Configuration.from("[1,2]");
		System.out.println(JSON.toJSONString(configuration.getKeys()));
		Assert.assertTrue(configuration.getKeys().contains("[0]"));
		Assert.assertTrue(configuration.getKeys().contains("[1]"));

		sets.clear();
		configuration = Configuration.from("[[[0]]]");
		System.out.println(JSON.toJSONString(configuration.getKeys()));
		Assert.assertTrue(configuration.getKeys().contains("[0][0][0]"));

		sets.clear();
		configuration = Configuration
				.from("{\"a\":{\"b\":{\"c\":[0],\"B\": \"B\"},\"A\": \"A\"}}");
		System.out.println(JSON.toJSONString(configuration.getKeys()));
		Assert.assertTrue(JSON.toJSONString(configuration.getKeys()).equals(
				"[\"a.b.B\",\"a.b.c[0]\",\"a.A\"]"));
	}

	@Test
	public void test_merge() {
		Configuration configuration = Configuration.from("{}");
		configuration.merge(Configuration.from("[1,2]"), true);
		System.out.println(configuration.toJSON());
		Assert.assertTrue(configuration.toJSON().equals("[1,2]"));

		configuration = Configuration.from("{}");
		configuration.merge(Configuration.from("[1,2]"), false);
		System.out.println(configuration.toJSON());
		Assert.assertTrue(configuration.toJSON().equals("[1,2]"));

		configuration = Configuration.from("{}");
		configuration.merge(Configuration.from("{\"1\": 2}"), true);
		System.out.println(configuration.toJSON());
		Assert.assertTrue(configuration.toJSON().equals("{\"1\":2}"));

		configuration = Configuration.from("{}");
		configuration.merge(Configuration.from("{\"1\": 2}"), false);
		System.out.println(configuration.toJSON());
		Assert.assertTrue(configuration.toJSON().equals("{\"1\":2}"));

		configuration = Configuration.from("{}");
		configuration.merge(Configuration.from("{\"1\":\"2\"}"), true);
		System.out.println(configuration.toJSON());
		Assert.assertTrue(configuration.toJSON().equals("{\"1\":\"2\"}"));

		configuration = Configuration.from("{}");
		configuration.merge(Configuration.from("{\"1\":\"2\"}"), false);
		System.out.println(configuration.toJSON());
		Assert.assertTrue(configuration.toJSON().equals("{\"1\":\"2\"}"));

		configuration = Configuration
				.from("{\"a\":{\"b\":{\"c\":[0],\"B\": \"B\"},\"A\": \"A\"}}");
		configuration
				.merge(Configuration
						.from("{\"a\":{\"b\":{\"c\":[\"bazhen\"],\"B\": \"B\"},\"A\": \"A\"}}"),
						true);
		System.out.println(configuration.toJSON());
		Assert.assertTrue(configuration.toJSON().equals(
				"{\"a\":{\"A\":\"A\",\"b\":{\"B\":\"B\",\"c\":[\"bazhen\"]}}}"));

		configuration = Configuration
				.from("{\"a\":{\"b\":{\"c\":[0],\"B\": \"B\"},\"A\": \"A\"}}");
		configuration
				.merge(Configuration
						.from("{\"a\":{\"b\":{\"c\":[\"bazhen\"],\"B\": \"B\",\"C\": \"C\"}}}"),
						false);
		System.out.println(configuration.toJSON());
		Assert.assertTrue(configuration
				.toJSON()
				.equals("{\"a\":{\"A\":\"A\",\"b\":{\"B\":\"B\",\"C\":\"C\",\"c\":[0]}}}"));
	}

	@Test
	public void test_type() {
		Configuration configuration = Configuration.from("{\"a\": 1}");
		Assert.assertTrue(configuration.getLong("a") == 1);
	}

	@Test
	public void test_beautify() {
		Configuration configuration = Configuration
				.from(ConfigurationTest.class.getClassLoader()
						.getResourceAsStream("all.json"));
		System.out.println(configuration.getConfiguration("job.content")
				.beautify());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void test() {
		Configuration configuration = Configuration
				.from(ConfigurationTest.class.getClassLoader()
						.getResourceAsStream("all.json"));
		System.out.println(configuration.toJSON());
		configuration.merge(Configuration.from(ConfigurationTest.class
				.getClassLoader().getResourceAsStream("all.json")), true);
		Assert.assertTrue(((List<Object>) configuration
				.get("job.content[0].reader.parameter.jdbcUrl")).size() == 2);

	}

	@Test(expected = IllegalArgumentException.class)
	public void test_remove() {
		Configuration configuration = Configuration.from("{\"a\": \"b\"}");
		configuration.remove("a");
		System.out.println(configuration.toJSON());
		Assert.assertTrue(configuration.toJSON().equals("{}"));

		configuration.set("a[1]", "b");
		System.out.println(configuration.toJSON());
		configuration.remove("a[1]");
		System.out.println(configuration.toJSON());
		Assert.assertTrue(configuration.toJSON().equals("{\"a\":[null,null]}"));

		configuration.set("a", "b");
		configuration.remove("b");
	}

	@Test
	public void test_unescape() {
		Configuration configuration = Configuration.from("{\"a\": \"\\t\"}");
		System.out.println("|" + configuration.getString("a") + "|");
		Assert.assertTrue("|\t|".equals("|" + configuration.getString("a")
				+ "|"));

		configuration = Configuration.from("{\"a\": \"\u0001\"}");
		Assert.assertTrue(configuration.getString("a").equals("\u0001"));
		Assert.assertTrue(new String(new byte[] { 0x01 }).equals(configuration
				.get("a")));

	}
}
